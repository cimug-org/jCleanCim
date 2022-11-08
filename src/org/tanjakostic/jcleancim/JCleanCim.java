/**
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic
 * <p>
 * This file belongs to jCleanCim, a tool supporting tasks of UML model managers for IEC TC57 CIM
 * and 61850 models.
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.tanjakostic.jcleancim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.gigipugni.jcleancim.mibgen.MibGen;
import org.tanjakostic.jcleancim.builder.EmptyModelBuilder;
import org.tanjakostic.jcleancim.builder.ModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.db.DbModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.japi.JapiModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.sqlxml.SqlXmlModelBuilder;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.ModelBuilderKind;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.WriterFactory;
import org.tanjakostic.jcleancim.docgen.collector.DocCollector;
import org.tanjakostic.jcleancim.docgen.collector.impl.DocCollectorImpl;
import org.tanjakostic.jcleancim.docgen.writer.Writer;
import org.tanjakostic.jcleancim.experimental.builder.xsd.ModelBuilderFromProfiles;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.statistics.CrossPackageStats;
import org.tanjakostic.jcleancim.statistics.ModelStats;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.validation.ModelValidator;

/**
 * jCleanCim command-line application.
 * <p>
 * Most of configuration currently needs to be specified in ./config/
 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file. Command line
 * arguments allow you to:
 * <ul>
 * <li>change the name of that file, i.e., to specify different configurations for different runs
 * with the same input model .eap, and/or</li>
 * <li>override the input model .eap file specified in configuration properties file - i.e., to use
 * the same configuration for different input models</li>
 * </ul>
 * <p>
 * We use apache command line argument library here as it gives nice help :-) If we need more
 * configuration/filtering, best would be to do that in ./config/
 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file and <b>not</b> with
 * command line options (we could end up in a mess of what is defined on cmd line, and what in
 * properties file).
 * <p>
 * Implementation note: To add new command line arguments, follow examples in the constructor. To
 * add functionality for validation, statistics, profile crosschek and doc generation (from UML or
 * from profiles), implement methods on {@link UmlModel} class and call them from
 * {@link #validate(UmlModel)}, {@link #collectStatistics(UmlModel)},
 * {@link #crossCheck(UmlModel, UmlModel)} and {@link #generateDoc(UmlModel)} methods, respectively.
 *
 * @author tatjana.kostic@ieee.org
 * @author Gian Luigi (Gigi) Pugni
 * @version $Id: JCleanCim.java 32 2019-12-13 09:29:11Z dev978 $
 */
public class JCleanCim {
	private static final Logger _logger = Logger.getLogger(JCleanCim.class.getName());

	private final Config _cfg;
	private final boolean _printHelp;
	private final boolean _printVersion;

	/**
	 * This command-line application first populates its model from full .eap file and/or profiles
	 * (to allow for different kinds of analysis afterwards), then selectively runs validation,
	 * statistics, profile-model cross-checking and MS Word document generation. These operations
	 * can be enabled/disabled and they apply to the scope as configured in ./config/
	 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file (or in a file
	 * you specify with <code>-propFile</code> command line option).
	 *
	 * <pre>
	 * usage: jCleanCim
	 *  -help                      print this message
	 *  -version                   print application version
	 *  -modelFile &lt;*.eap&gt;         name of the model file to use instead of one
	 *                             defined in config properties
	 *  -propFile &lt;*.properties&gt;   name of the config properties file to use
	 *                             instead of default
	 * </pre>
	 *
	 * @throws ApplicationException
	 */
	public static void main(String[] args) throws ApplicationException, IOException {
		_logger.log(Level.INFO, "started at: " + new Date());

		long start = System.currentTimeMillis();
		logExecutionEnvironment(Level.INFO);
		logApplicationVersion();

		JCleanCim app = new JCleanCim(args);
		if (!(app.isPrintHelpOnly() || app.isPrintVersionOnly())) {

			Config cfg = app.getCfg();
			_logger.info(cfg);

			// pause(); // uncomment when profiling, to give time to the profiler to connect

			// initialise model(s); export to XMI, if enabled, has to be done here as well:
			UmlModel umlModel = cfg.hasUmlModel() ? app.buildFromEA() : app.createEmptyModel();
			UmlModel profilesModel = (cfg.isProfilesCrosscheckOn() || cfg.isProfilesDocgenOn())
					? app.buildFromProfiles()
					: null;

			// run what has been configured:
			if (cfg.isValidationOn()) {
				app.validate(umlModel);
			}
			if (cfg.isStatisticsOn()) {
				app.collectStatistics(umlModel);
			}
			if (cfg.isProfilesCrosscheckOn()) {
				app.crossCheck(profilesModel, umlModel);
			}

			if (cfg.isMibgenOn()) {
				app.generateMib(umlModel);
			}

			if (cfg.isProfilesDocgenOn()) {
				app.generateDoc(profilesModel);
			} else if (cfg.isDocgenModelOn()) {
				app.generateDoc(umlModel);
			}

			Util.logCompletion(Level.INFO, "completed all configured steps - exiting", start,
					cfg.isAppSkipTiming());
		}
		_logger.info("completed at: " + new Date());
		_logger.info("exiting");
	}

	static void pause() throws IOException {
		_logger.warn("Press any key to continue ...");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		in.readLine();
	}

	/**
	 * Constructor.
	 * <p>
	 * Implementation note: If you add command line arguments, run jCleanCim with only -help command
	 * line argument and update the doc of {@link #main(String[])} with what is printed on screen.
	 *
	 * @param args
	 * @throws ApplicationException
	 */
	private JCleanCim(String[] args) throws ApplicationException {
		Options options = new Options();
		Option helpOp = new Option("help", "print this message");
		Option versionOp = new Option("version", "print application version");
		OptionBuilder.withArgName("*.properties");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("name of the config properties file to use instead of default");
		Option propFileOp = OptionBuilder.create("propFile");
		OptionBuilder.withArgName("*.eap");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(
				"name of the model file to use instead of one defined in config properties");
		Option modelFileOp = OptionBuilder.create("modelFile");
		options.addOption(helpOp);
		options.addOption(versionOp);
		options.addOption(propFileOp);
		options.addOption(modelFileOp);

		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			throw new ApplicationException(e.getMessage(), e);
		}
		_printHelp = cmd.hasOption(helpOp.getOpt());
		_printVersion = cmd.hasOption(versionOp.getOpt());
		if (_printHelp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("jCleanCim", options);

			_cfg = null;
		} else if (_printVersion) {
			// do nothing

			_cfg = null;
		} else {
			String propFileName = cmd.hasOption(propFileOp.getOpt())
					? cmd.getOptionValue(propFileOp.getOpt())
					: null;
			String modelFileName = cmd.hasOption(modelFileOp.getOpt())
					? cmd.getOptionValue(modelFileOp.getOpt())
					: null;

			_cfg = new Config(propFileName, modelFileName);
		}
	}

	private static void logExecutionEnvironment(Level level) {
		Properties sysProps = System.getProperties();
		_logger.log(level, "execution environment:");
		_logger.log(level, "  os.name = " + sysProps.get("os.name"));
		_logger.log(level, "  java.version = " + sysProps.get("java.version"));
		_logger.log(level, "  java.home = " + sysProps.get("java.home"));
		_logger.log(level, "  java.class.path = " + sysProps.get("java.class.path"));
	}

	private static void logApplicationVersion() {
		_logger.info("jCleanCim version: " + Config.deduceAppVersion());
		_logger.info("");
	}

	/**
	 * Builds the model from EA file given in configuration or on command line.
	 *
	 * @throws ApplicationException
	 */
	public UmlModel buildFromEA() throws ApplicationException {
		ModelBuilderKind builderKind = getCfg().getModelBuilder();

		Util.logTitle(Level.INFO,
				String.format("building model from EA %s...", builderKind.getText()));
		long start = System.currentTimeMillis();

		ModelBuilder builder = null;
		switch (builderKind) {
			case sqlxml:
				builder = new SqlXmlModelBuilder(getCfg());
				break;
			case japi:
				builder = new JapiModelBuilder(getCfg());
				break;
			case db:
			default:
				builder = new DbModelBuilder(getCfg());
		}
		UmlModel umlModel = builder.build();

		Util.logCompletion(Level.INFO,
				String.format("built model from '%s'", umlModel.getModelNamesWithNature()), start,
				getCfg().isAppSkipTiming());
		return umlModel;
	}

	/**
	 * Builds the model from all profiles found under the /input/profiles directory.
	 *
	 * @throws ApplicationException
	 */
	public UmlModel buildFromProfiles() throws ApplicationException {
		Util.logTitle(Level.INFO, "building model from profiles...");
		long start = System.currentTimeMillis();

		ModelBuilder builder = new ModelBuilderFromProfiles(getCfg());
		UmlModel profilesModel = builder.build();

		Util.logCompletion(Level.INFO,
				String.format("built model from profiles %s.",
						profilesModel.getModelNamesWithNature()),
				start, getCfg().isAppSkipTiming());
		return profilesModel;
	}

	/**
	 * Creates empty model.
	 *
	 * @throws ApplicationException
	 */
	public UmlModel createEmptyModel() throws ApplicationException {
		Util.logTitle(Level.INFO, "creating empty model...");
		long start = System.currentTimeMillis();

		ModelBuilder builder = new EmptyModelBuilder(getCfg());
		UmlModel umlModel = builder.build();

		Util.logCompletion(Level.INFO, "created empty model", start, getCfg().isAppSkipTiming());
		return umlModel;
	}

	/**
	 * Validates the model.
	 *
	 * @param model
	 */
	public void validate(UmlModel model) {
		String scope = getCfg().getValidationScope().toString();
		Util.logTitle(Level.INFO, String.format("validating packages %s...", scope));
		long start = System.currentTimeMillis();

		ModelValidator validator = new ModelValidator(model);
		validator.logAllAvailableRuleNames(Level.INFO);
		validator.logAvailableRuleNamesWithCategoryAndSeverity(Level.DEBUG);
		validator.validate();
		validator.saveReport();

		Util.logCompletion(Level.INFO, String.format("validated packages %s.", scope), start,
				getCfg().isAppSkipTiming());
	}

	/**
	 * Collects statistics for the model and logs them.
	 *
	 * @param model
	 */
	public void collectStatistics(UmlModel model) {
		String scope = getCfg().getValidationScope().toString();
		Util.logTitle(Level.INFO, String.format("collecting statistics for packages %s...", scope));
		long start = System.currentTimeMillis();

		ModelStats stats = new ModelStats(model);
		stats.logStats();

		CrossPackageStats cpStats = new CrossPackageStats(model);
		cpStats.logStats();

		stats.logPackages(Level.DEBUG);
		stats.logClasses(Level.DEBUG);
		stats.logOperations(Level.DEBUG);

		// TODO: Uncomment this to log WG14 normative stuff
		// stats.logNormativeClasses(Level.INFO, EnumSet.of(OwningWg.WG14));
		// stats.logNormativeAssociationsWithWgClasses(Level.INFO, OwningWg.WG14);

		stats.logAggregationsWithWgClasses(Level.INFO, OwningWg.WG14);

		stats.logCimNoncimAssociations(Level.INFO); // applies when 61850 and mappings present
		stats.logClassesWithAttributeConstraints(Level.INFO);
		stats.logAttributesWithConstraints(Level.INFO);
		stats.logMultivaluedAttributes(Level.INFO);
		stats.logNamespaceInfos(Level.INFO);
		stats.logVersionInfos(Level.INFO);
		stats.logTaggedValues(Level.INFO);
		stats.logDONameDecomposition(Level.INFO);
		stats.logAbbreviatedTermUsage(Level.INFO);

		Util.logCompletion(Level.INFO,
				String.format("collected statistics for %s packages.", scope), start,
				getCfg().isAppSkipTiming());
	}

	/**
	 * Performs cross-check between the set of profiles and the UML model.
	 *
	 * @param profilesModel
	 * @param umlModel
	 */
	public void crossCheck(UmlModel profilesModel, UmlModel umlModel) {
		Util.logTitle(Level.INFO,
				String.format("cross-checking profile(s) %s against UML model '%s'...",
						profilesModel.getModelNamesWithNature(),
						umlModel.getModelNamesWithNature()));
		long start = System.currentTimeMillis();

		umlModel.crossCheck(profilesModel);

		Util.logCompletion(Level.INFO,
				String.format("cross-checked profile(s) %s against UML model '%s'.",
						profilesModel.getModelNamesWithNature(),
						umlModel.getModelNamesWithNature()),
				start, getCfg().isAppSkipTiming());
	}

	/**
	 * Generates full and light MIBs from the model.
	 *
	 * @throws UnsupportedOutputFormatException
	 *             if the requested format (extension) of the output file is not supported.
	 * @throws IOException
	 *             on any file system-related problem.
	 */
	public void generateMib(UmlModel model) throws ApplicationException, IOException {
		Util.logTitle(Level.INFO, "generating MIBs from " + model.getModelNamesWithNature());
		long start = System.currentTimeMillis();

		// FIXME: split on collect + write

		Util.logSubtitle(Level.INFO, "   full MIBs ...");
		MibGen mibgen = new MibGen(model);
		mibgen.collectMib(model, false);

		Util.logSubtitle(Level.INFO, "   light MIBs ...");
		mibgen = new MibGen(model);
		mibgen.collectMib(model, true);

		Util.logCompletion(Level.INFO,
				"generated MIBs in: " + getCfg().getMibgenOutDirFullAbsPath(), start,
				getCfg().isAppSkipTiming());
	}

	/**
	 * Generates documentation for the model in the format specified in configuration through output
	 * file extension.
	 *
	 * @throws UnsupportedOutputFormatException
	 *             if the requested format (extension) of the output file is not supported.
	 * @throws IOException
	 *             on any file system-related problem.
	 */
	public void generateDoc(UmlModel model) throws ApplicationException, IOException {
		Util.logTitle(Level.INFO, "generating doc from " + model.getModelNamesWithNature());
		long start = System.currentTimeMillis();

		Util.logSubtitle(Level.INFO, "collecting documentation content ...");
		DocCollector collector = new DocCollectorImpl(model);
		collector.collect(model);
		Util.logCompletion(Level.INFO,
				String.format("collected documentation content for '%s'.", collector.toString()),
				start, getCfg().isAppSkipTiming());

		Util.logSubtitle(Level.INFO, "writing documentation ...");
		start = System.currentTimeMillis();
		Writer writer = WriterFactory.createWriter(model.getCfg(), collector);
		_logger.info("  from " + writer.getInputFileNames());
		_logger.info("  into " + writer.getOutputFileNames());
		writer.write();
		Util.logCompletion(Level.INFO, "written documentation to " + writer.getOutputFileNames(),
				start, getCfg().isAppSkipTiming());
	}

	private boolean isPrintHelpOnly() {
		return _printHelp;
	}

	private boolean isPrintVersionOnly() {
		return _printVersion;
	}

	private Config getCfg() {
		return _cfg;
	}
}
