/*
 * Copyright (c) 2010. Stephen Connolly.
 * Copyright (c) 2006. Björn Stickler <bjoern@stickler.de>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.morpheusdata.javaisotools.udflib.structures;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.morpheusdata.javaisotools.udflib.tools.BinaryTools;

public class Tag {

    public int TagIdentifier;            // Uint16
    public int DescriptorVersion;        // Uint16
    public short TagChecksum;            // Uint8
    public byte Reserved;                // byte
    public int TagSerialNumber;        // Uint16
    public int DescriptorCRC;            // Uint16
    public int DescriptorCRCLength;    // Uint16
    public long TagLocation;            // Uint32

    public void read(RandomAccessFile myRandomAccessFile)
            throws IOException {
        TagIdentifier = BinaryTools.readUInt16AsInt(myRandomAccessFile);
        DescriptorVersion = BinaryTools.readUInt16AsInt(myRandomAccessFile);
        TagChecksum = (short) myRandomAccessFile.readUnsignedByte();
        Reserved = myRandomAccessFile.readByte();
        TagSerialNumber = BinaryTools.readUInt16AsInt(myRandomAccessFile);
        DescriptorCRC = BinaryTools.readUInt16AsInt(myRandomAccessFile);
        DescriptorCRCLength = BinaryTools.readUInt16AsInt(myRandomAccessFile);
        TagLocation = BinaryTools.readUInt32AsLong(myRandomAccessFile);
    }

    public int read(byte[] rawData, int startPosition) {
        int position = startPosition;
        TagIdentifier = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256;
        DescriptorVersion = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256;
        TagChecksum = (short) (rawData[position++] & 0xFF);
        Reserved = rawData[position++];
        TagSerialNumber = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256;
        DescriptorCRC = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256;
        DescriptorCRCLength = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256;
        TagLocation = (rawData[position++] & 0xFF) + (rawData[position++] & 0xFF) * 256 +
                (rawData[position++] & 0xFF) * 256 * 256 + (rawData[position++] & 0xFF) * 256 * 256 * 256;
        return position;
    }

    public void write(RandomAccessFile myRandomAccessFile)
            throws IOException {
        myRandomAccessFile.write(getBytes());
    }

    public byte[] getBytes() {
        TagChecksum = calculateChecksum();

        byte rawBytes[] = new byte[16];

        int pos = 0;

        pos = BinaryTools.getUInt16BytesFromInt(TagIdentifier, rawBytes, pos);
        pos = BinaryTools.getUInt16BytesFromInt(DescriptorVersion, rawBytes, pos);

        rawBytes[pos++] = (byte) (TagChecksum & 0xFF);

        rawBytes[pos++] = Reserved;

        pos = BinaryTools.getUInt16BytesFromInt(TagSerialNumber, rawBytes, pos);
        pos = BinaryTools.getUInt16BytesFromInt(DescriptorCRC, rawBytes, pos);
        pos = BinaryTools.getUInt16BytesFromInt(DescriptorCRCLength, rawBytes, pos);
        pos = BinaryTools.getUInt32BytesFromLong(TagLocation, rawBytes, pos);

        return rawBytes;
    }

    public short calculateChecksum() {
        short checksum = 0;

        checksum += TagIdentifier & 0xFF;
        checksum += (TagIdentifier >> 8) & 0xFF;

        checksum += DescriptorVersion & 0xFF;
        checksum += (DescriptorVersion >> 8) & 0xFF;

        checksum += Reserved;

        checksum += TagSerialNumber & 0xFF;
        checksum += (TagSerialNumber >> 8) & 0xFF;

        checksum += DescriptorCRC & 0xFF;
        checksum += (DescriptorCRC >> 8) & 0xFF;

        checksum += DescriptorCRCLength & 0xFF;
        checksum += (DescriptorCRCLength >> 8) & 0xFF;

        checksum += TagLocation & 0xFF;
        checksum += (TagLocation >> 8) & 0xFF;
        checksum += (TagLocation >> 16) & 0xFF;
        checksum += (TagLocation >> 24) & 0xFF;

        return (short) (checksum & 0xFF);
    }
}
