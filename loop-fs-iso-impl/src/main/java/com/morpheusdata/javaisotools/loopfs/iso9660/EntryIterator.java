/*
 * Copyright (c) 2010. Stephen Connolly.
 * Copyright (c) 2006-2007. loopy project (http://loopy.sourceforge.net).
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.morpheusdata.javaisotools.loopfs.iso9660;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.morpheusdata.javaisotools.loopfs.util.LittleEndian;

/**
 * A breadth-first iterator of the entries in a ISO9660 file system.
 */
class EntryIterator implements Iterator<Iso9660FileEntry> {

    private final Iso9660FileSystem fileSystem;
    private final List<Iso9660FileEntry> queue;

    public EntryIterator(final Iso9660FileSystem fileSystem, final Iso9660FileEntry rootEntry) {
        this.fileSystem = fileSystem;
        this.queue = new LinkedList<Iso9660FileEntry>();
        if (rootEntry != null)
        	this.queue.add(rootEntry);
    }

    public boolean hasNext() {
        return !this.queue.isEmpty();
    }

    public Iso9660FileEntry next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // pop next entry from the queue
        final Iso9660FileEntry entry = this.queue.remove(0);

        // if the entry is a directory, queue all its children
        if (entry.isDirectory()) {
            final byte[] content;

            try {
                content = this.fileSystem.getBytes(entry);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            int offset = 0;
            boolean paddingMode = false;

            while (offset < content.length) {
                if (LittleEndian.getUInt8(content, offset) <= 0) {
                    paddingMode = true;
                    offset += 2;
                    continue;
                }

                Iso9660FileEntry child = new Iso9660FileEntry(
                        this.fileSystem, entry.getPath(), content, offset + 1);

                if (paddingMode && child.getSize() < 0) {
                    continue;
                }

                offset += child.getEntryLength();

                // It doesn't seem useful to include the . and .. entries
                if (!".".equals(child.getName()) && !"..".equals(child.getName())) {
                    this.queue.add(child);
                }
            }
        }

        return entry;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
