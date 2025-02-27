/*
 * Copyright (c) 2010. Stephen Connolly.
 * Copyright (C) 2007. Jens Hatlak <hatlak@rbg.informatik.tu-darmstadt.de>
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

package com.morpheusdata.javaisotools.iso9660.sabre.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.morpheusdata.javaisotools.sabre.DataReference;

public class LSBFWordDataReference implements DataReference {

    private long value = 0;

    public LSBFWordDataReference(long value) {
        this.value = value;
    }

    public long getLength() {
        return 4;
    }

    public InputStream createInputStream() throws IOException {
        byte[] buffer = new byte[4];

        // LSB first (Little Endian)
        buffer[0] = (byte) (this.value & 0x000000FF);
        buffer[1] = (byte) ((this.value & 0x0000FF00) >> 8);
        buffer[2] = (byte) ((this.value & 0x00FF0000) >> 16);
        buffer[3] = (byte) ((this.value & 0xFF000000) >> 24);

        return new ByteArrayInputStream(buffer);
    }

}
