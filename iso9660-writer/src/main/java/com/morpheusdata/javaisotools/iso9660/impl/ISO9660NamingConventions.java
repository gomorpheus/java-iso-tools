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

package com.morpheusdata.javaisotools.iso9660.impl;

import java.util.Vector;

import com.morpheusdata.javaisotools.iso9660.ISO9660Directory;
import com.morpheusdata.javaisotools.iso9660.ISO9660File;
import com.morpheusdata.javaisotools.iso9660.NamingConventions;
import com.morpheusdata.javaisotools.sabre.HandlerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISO9660NamingConventions extends NamingConventions {

    public int interchangeLevel = 1;
    public boolean forceISO9660Charset = true;
    public boolean forceDotDelimiter = true;
    private boolean enforce8plus3;
    private int maxDirectoryLength, maxFilenameLength, maxExtensionLength;

    public ISO9660NamingConventions(ISO9660Config config) {
        super("ISO 9660");
        this.interchangeLevel = config.interchangeLevel;
        this.forceISO9660Charset = config.forceISO9660Charset;
        this.forceDotDelimiter = config.forceDotDelimiter;
     }

    public void apply(ISO9660Directory dir) throws HandlerException {
        // ISO 9660 directory restrictions:
        // - character set: uppercase letters, digits and underscore
        // - filename may NOT contain dot
        // - filename non-empty
        // - filename <= MAX_DIRECTORY_LENGTH bytes
        init();

        String filename = normalize(dir.getName());
        if (filename.length() > maxDirectoryLength) {
            filename = filename.substring(0, maxDirectoryLength);
        }

        if (filename.length() == 0) {
            throw new HandlerException(getID() + ": Empty directory name encountered.");
        }

        setFilename(dir, filename);
    }

    public void apply(ISO9660File file) throws HandlerException {
        // ISO 9660 file name restrictions:
        // - character set: uppercase letters, digits, underscore, dot and semicolon
        // - filename + extension <= 30 bytes
        // - filename <= MAX_FILENAME_LENGTH
        // - extension <= MAX_EXTENSION_LENGTH
        // - either filename or extension must be non-empty
        // - file version must be present and delimited by semicolon
        enforce8plus3(file.enforces8plus3());
        init();

        String filename = normalize(file.getFilename());
        String extension = normalize(file.getExtension());
        file.enforceDotDelimiter(forceDotDelimiter);

        if (filename.length() == 0 && extension.length() == 0) {
            throw new HandlerException(getID() + ": Empty file name encountered.");
        }

        if (enforces8plus3()) {
            if (filename.length() > maxFilenameLength) {
                filename = filename.substring(0, maxFilenameLength);
            }
            if (extension.length() > maxExtensionLength) {
                String mapping = getExtensionMapping(extension);
                if (mapping != null && mapping.length() <= maxExtensionLength) {
                    extension = normalize(mapping);
                } else {
                    extension = extension.substring(0, maxExtensionLength);
                }
            }
        } else {
            // See ISO 9660:7.5.1
            if (filename.length() + extension.length() > 30) {
                if (filename.length() >= extension.length()) {
                    // Shorten filename
                    filename = filename.substring(0, 30 - extension.length());
                } else {
                    // Shorten extension
                    String mapping = getExtensionMapping(extension);
                    if (mapping != null && mapping.length() <= maxExtensionLength) {
                        extension = normalize(mapping);
                    } else {
                        extension = extension.substring(0, 30 - filename.length());
                    }
                }
            }
        }

        setFilename(file, filename, extension);
    }

    public void init() {
        if (interchangeLevel == 1) {
            enforce8plus3(true);
        }

        if (enforces8plus3()) {
            // Interchange Level 1 (or explicitly requested): Directories 8, files 8+3 characters
            maxDirectoryLength = 8;
            maxFilenameLength = 8;
            maxExtensionLength = 3;
        } else {
            maxDirectoryLength = 31;
            maxFilenameLength = 0;
            maxExtensionLength = 0;
        }
    }

    void enforce8plus3(boolean flag) {
        this.enforce8plus3 = flag;
    }

    boolean enforces8plus3() {
        return enforce8plus3;
    }

    private String normalize(String name) {
        if (forceISO9660Charset) {
            name = name.toUpperCase();
            return name.replaceAll("[^A-Z0-9_]", "_");
        } // else

        // Note: Backslash escaped for both the RegEx and Java itself
        return name.replaceAll("[*/:;?\\\\]", "_");
    }

    public void addDuplicate(Vector duplicates, String name, int version) {
        String[] data = {name.toUpperCase(), version + ""};
        duplicates.add(data);
    }

    public boolean checkFilenameEquality(String name1, String name2) {
        return name1.equalsIgnoreCase(name2);
    }

    public void checkPathLength(String isoPath) {
        // ISO 9660:6.8.2.1: 255 Byte (255 characters)
        if (isoPath.length() > 255) {
            log.warn("{}: Path length exceeds limit: {}", getID(), isoPath);
        }
    }
}
