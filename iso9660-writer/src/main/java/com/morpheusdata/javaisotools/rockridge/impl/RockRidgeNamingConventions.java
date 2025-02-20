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

package com.morpheusdata.javaisotools.rockridge.impl;

import java.util.Vector;

import com.morpheusdata.javaisotools.iso9660.ISO9660File;
import com.morpheusdata.javaisotools.iso9660.ISO9660Directory;
import com.morpheusdata.javaisotools.iso9660.NamingConventions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RockRidgeNamingConventions extends NamingConventions {

    public boolean hideMovedDirectoriesStore = true;
    public boolean forcePortableFilenameCharacterSet = true;
    // Filename lengths are not restricted by Rock Ridge,
    // these are just safe defaults
    public int maxDirectoryLength = 255;
    public int maxFilenameLength = 255;

    public RockRidgeNamingConventions(RockRidgeConfig rockRidgeConfig) {
        super("Rock Ridge");
        this.hideMovedDirectoriesStore = rockRidgeConfig.hideMovedDirectoriesStore;
        this.forcePortableFilenameCharacterSet = rockRidgeConfig.forcePortableFilenameCharacterSet;
        this.maxDirectoryLength = rockRidgeConfig.maxDirectoryLength;
        this.maxFilenameLength = rockRidgeConfig.maxFilenameLength;
    }

    public void apply(ISO9660Directory dir) {
        String filename = normalize(dir.getName());

        if (filename.length() > maxDirectoryLength) {
            // Shorten filename
            filename = filename.substring(0, maxDirectoryLength);
        }

        setFilename(dir, filename);
    }

    public void apply(ISO9660File file) {
        String filename = normalize(file.getFilename());
        String extension = normalize(file.getExtension());
        int length = filename.length() + extension.length();

        if (extension.length() == 0) {
            if (length > maxFilenameLength) {
                // Shorten filename
                filename = filename.substring(0, maxFilenameLength);
            }
        } else {
            if (length + 1 > maxFilenameLength) {
                // Shorten filename
                filename = filename.substring(0, maxFilenameLength - extension.length() - 1);
            }
        }

        setFilename(file, filename, extension);
    }

    public boolean checkDuplicate(Vector duplicates, String name, int version) {
        return checkDuplicate(duplicates, name, version, false);
    }

    public void endRenaming(ISO9660File file) {
        if (VERBOSE) {
            log.info(" to {}", file.getName());
        }
    }

    private String normalize(String name) {
        if (forcePortableFilenameCharacterSet) {
            return name.replaceAll("[^-A-Za-z0-9._]", "_");
        }
        return name;
    }

    public void checkPathLength(String isoPath) {
        // Nothing to do here (Rock Ridge has no own path length constraint -> ISO 9660 check is sufficient)
    }
}
