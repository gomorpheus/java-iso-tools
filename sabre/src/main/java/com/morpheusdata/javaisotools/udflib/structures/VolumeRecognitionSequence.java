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

public class VolumeRecognitionSequence {

    public enum NSRVersion {

        NSR02,
        NSR03
    }

    private NSRVersion nsrVersion;

    public VolumeRecognitionSequence(NSRVersion nsrVersion) {
        this.nsrVersion = nsrVersion;
    }

    public void write(RandomAccessFile myRandomAccessFile)
            throws IOException {
        VolumeStructureDescriptor beginningExtendedAreaDescriptor = new VolumeStructureDescriptor();
        beginningExtendedAreaDescriptor.StructureType = 0;
        beginningExtendedAreaDescriptor.StandardIdentifier = new byte[]{'B', 'E', 'A', '0', '1'};
        beginningExtendedAreaDescriptor.StructureVersion = 1;
        beginningExtendedAreaDescriptor.write(myRandomAccessFile);

        VolumeStructureDescriptor NSRDescriptor = new VolumeStructureDescriptor();
        NSRDescriptor.StructureType = 0;
        NSRDescriptor.StructureVersion = 1;

        if (nsrVersion == NSRVersion.NSR02) {
            NSRDescriptor.StandardIdentifier = new byte[]{'N', 'S', 'R', '0', '2'};
        } else if (nsrVersion == NSRVersion.NSR03) {
            NSRDescriptor.StandardIdentifier = new byte[]{'N', 'S', 'R', '0', '3'};
        }

        NSRDescriptor.write(myRandomAccessFile);

        VolumeStructureDescriptor terminatingExtendedAreaDescriptor = new VolumeStructureDescriptor();
        terminatingExtendedAreaDescriptor.StructureType = 0;
        terminatingExtendedAreaDescriptor.StandardIdentifier = new byte[]{'T', 'E', 'A', '0', '1'};
        terminatingExtendedAreaDescriptor.StructureVersion = 1;
        terminatingExtendedAreaDescriptor.write(myRandomAccessFile);
    }

    public byte[] getBytes() {
        VolumeStructureDescriptor beginningExtendedAreaDescriptor = new VolumeStructureDescriptor();
        beginningExtendedAreaDescriptor.StructureType = 0;
        beginningExtendedAreaDescriptor.StandardIdentifier = new byte[]{'B', 'E', 'A', '0', '1'};
        beginningExtendedAreaDescriptor.StructureVersion = 1;

        byte[] beginningExtendedAreaDescriptorBytes = beginningExtendedAreaDescriptor.getBytes();

        VolumeStructureDescriptor NSRDescriptor = new VolumeStructureDescriptor();
        NSRDescriptor.StructureType = 0;
        NSRDescriptor.StructureVersion = 1;

        if (nsrVersion == NSRVersion.NSR02) {
            NSRDescriptor.StandardIdentifier = new byte[]{'N', 'S', 'R', '0', '2'};
        } else if (nsrVersion == NSRVersion.NSR03) {
            NSRDescriptor.StandardIdentifier = new byte[]{'N', 'S', 'R', '0', '3'};
        }

        byte[] NSRDescriptorBytes = NSRDescriptor.getBytes();

        VolumeStructureDescriptor terminatingExtendedAreaDescriptor = new VolumeStructureDescriptor();
        terminatingExtendedAreaDescriptor.StructureType = 0;
        terminatingExtendedAreaDescriptor.StandardIdentifier = new byte[]{'T', 'E', 'A', '0', '1'};
        terminatingExtendedAreaDescriptor.StructureVersion = 1;

        byte[] terminatingExtendedAreaDescriptorBytes = terminatingExtendedAreaDescriptor.getBytes();

        byte[] rawBytes = new byte[beginningExtendedAreaDescriptorBytes.length
                + NSRDescriptorBytes.length
                + terminatingExtendedAreaDescriptorBytes.length];

        int pos = 0;

        System.arraycopy(beginningExtendedAreaDescriptorBytes, 0, rawBytes, pos,
                beginningExtendedAreaDescriptorBytes.length);
        pos += beginningExtendedAreaDescriptorBytes.length;

        System.arraycopy(NSRDescriptorBytes, 0, rawBytes, pos, NSRDescriptorBytes.length);
        pos += NSRDescriptorBytes.length;

        System.arraycopy(terminatingExtendedAreaDescriptorBytes, 0, rawBytes, pos,
                terminatingExtendedAreaDescriptorBytes.length);
        pos += terminatingExtendedAreaDescriptorBytes.length;

        return rawBytes;
    }

}
