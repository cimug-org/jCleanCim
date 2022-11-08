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

package org.tanjakostic.jcleancim.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class copied (with pride :-) and adapted from: <a
 * href="http://www.javaworld.com/javaworld/javatips/jw-javatip60.html">Java World</a>.
 * <p>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: BMPFile.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class BMPFile extends Component {
	private static final long serialVersionUID = 1L;

	private final static int BITMAPFILEHEADER_SIZE = 14;
	private final static int BITMAPINFOHEADER_SIZE = 40;

	// --- Bitmap file header
	private final byte _fType[] = { 'B', 'M' };
	private int _fSize = 0;
	private final int _fReserved1 = 0;
	private final int _fReserved2 = 0;
	private final int _fOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;

	// --- Bitmap info header
	private final int _infoHeaderSize = BITMAPINFOHEADER_SIZE;
	private int _width = 0;
	private int _height = 0;
	private static final int planes = 1;
	private static final int bitcount = 24;
	private static final int compression = 0;
	private int _sizeImage = 0x030000;
	private static final int xPelsPerMeter = 0x0;
	private static final int yPelsPerMeter = 0x0;
	private static final int clrUsed = 0;
	private static final int clrImportant = 0;

	// --- Bitmap raw data
	private int bitmap[];

	public BMPFile() {
		// default constructor
	}

	public void saveBitmap(String filename, Image image, int width, int height) throws IOException {
		try (FileOutputStream fo = new FileOutputStream(filename)) {
			convertImage(image, width, height);
			writeBitmapFileHeader(fo);
			writeBitmapInfoHeader(fo);
			writeBitmap(fo);
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Converts the memory image to the bitmap format (BRG). It also computes some information for
	 * the bitmap info header.
	 */
	private boolean convertImage(Image parImage, int parWidth, int parHeight) {
		bitmap = new int[parWidth * parHeight];
		PixelGrabber pg = new PixelGrabber(parImage, 0, 0, parWidth, parHeight, bitmap, 0, parWidth);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		int pad = (4 - ((parWidth * 3) % 4)) * parHeight;
		_sizeImage = ((parWidth * parHeight) * 3) + pad;
		_fSize = _sizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
		_width = parWidth;
		_height = parHeight;
		return true;
	}

	/**
	 * Converts the image returned from the pixel grabber to the format required. Remember: scan
	 * lines are inverted in a bitmap file! Each scan line must be padded to an even 4-byte
	 * boundary.
	 *
	 * @throws IOException
	 */
	private void writeBitmap(FileOutputStream fo) throws IOException {
		byte[] rgb = new byte[3];
		int size = (_width * _height) - 1;
		int pad = 4 - ((_width * 3) % 4);
		if (pad == 4) {
			pad = 0; // <==== Bug correction
		}
		int rowCount = 1;
		int padCount = 0;
		int rowIndex = size - _width;
		int lastRowIndex = rowIndex;
		for (int j = 0; j < size; j++) {
			int value = bitmap[rowIndex];
			rgb[0] = (byte) (value & 0xFF);
			rgb[1] = (byte) ((value >> 8) & 0xFF);
			rgb[2] = (byte) ((value >> 16) & 0xFF);
			fo.write(rgb);
			if (rowCount == _width) {
				padCount += pad;
				for (int i = 1; i <= pad; i++) {
					fo.write(0x00);
				}
				rowCount = 1;
				rowIndex = lastRowIndex - _width;
				lastRowIndex = rowIndex;
			} else {
				rowCount++;
			}
			rowIndex++;
		}
		// --- Update the size of the file
		_fSize += padCount - pad;
		_sizeImage += padCount - pad;
	}

	/**
	 * Writes the bitmap file header to the file.
	 *
	 * @throws IOException
	 */
	private void writeBitmapFileHeader(FileOutputStream fo) throws IOException {
		fo.write(_fType);
		fo.write(intToDWord(_fSize));
		fo.write(intToWord(_fReserved1));
		fo.write(intToWord(_fReserved2));
		fo.write(intToDWord(_fOffBits));
	}

	/**
	 * Writes the bitmap information header to the file.
	 *
	 * @throws IOException
	 */
	private void writeBitmapInfoHeader(FileOutputStream fo) throws IOException {
		fo.write(intToDWord(_infoHeaderSize));
		fo.write(intToDWord(_width));
		fo.write(intToDWord(_height));
		fo.write(intToWord(planes));
		fo.write(intToWord(bitcount));
		fo.write(intToDWord(compression));
		fo.write(intToDWord(_sizeImage));
		fo.write(intToDWord(xPelsPerMeter));
		fo.write(intToDWord(yPelsPerMeter));
		fo.write(intToDWord(clrUsed));
		fo.write(intToDWord(clrImportant));
	}

	/**
	 * Converts an int to a word, where the return value is stored in a 2-byte array.
	 */
	private byte[] intToWord(int parValue) {
		byte result[] = new byte[2];
		result[0] = (byte) (parValue & 0x00FF);
		result[1] = (byte) ((parValue >> 8) & 0x00FF);
		return result;
	}

	/**
	 * Converts an int to a double word, where the return value is stored in a 4-byte array.
	 */
	private byte[] intToDWord(int parValue) {
		byte result[] = new byte[4];
		result[0] = (byte) (parValue & 0x00FF);
		result[1] = (byte) ((parValue >> 8) & 0x000000FF);
		result[2] = (byte) ((parValue >> 16) & 0x000000FF);
		result[3] = (byte) ((parValue >> 24) & 0x000000FF);
		return result;
	}
}
