/*
 * Copyright (c) 2010. Stephen Connolly.
 * Copyright (c) 2006. Michael Hartle <mhartle@rbg.informatik.tu-darmstadt.de>.
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

package com.morpheusdata.javaisotools.sabre.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.morpheusdata.javaisotools.sabre.DataReference;

public class ByteArrayDataReference implements DataReference {

    private byte[] buffer = null;
    private int start = 0;
    private int length = 0;

    public ByteArrayDataReference(byte[] buffer) {
        this.buffer = buffer;
        this.start = 0;
        this.length = this.buffer.length;
    }

    public ByteArrayDataReference(byte[] buffer, int start, int length) {
        this.buffer = buffer;
        this.start = start;
        this.length = length;
    }

    public long getLength() {
        return this.length;
    }

    public InputStream createInputStream() throws IOException {
        return new ByteArrayInputStream(this.buffer, this.start, this.length);
    }

}
