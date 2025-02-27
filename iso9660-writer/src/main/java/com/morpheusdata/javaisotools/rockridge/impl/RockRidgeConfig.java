/*
 * Copyright (c) 2013. Brad BARCLAY <brad.barclay@infor.com>
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

import com.morpheusdata.javaisotools.iso9660.ConfigException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RockRidgeConfig {
    public boolean hideMovedDirectoriesStore = true;
    public boolean forcePortableFilenameCharacterSet = true;
    // Filename lengths are not restricted by Rock Ridge,
    // these are just safe defaults
    public int maxDirectoryLength = 255;
    public int maxFilenameLength = 255;

    public boolean isHideMovedDirectoriesStore() {
        return hideMovedDirectoriesStore;
    }

    private Map<String, Integer> patternToModeMap = new HashMap<String, Integer>();
    
    /**
     * Set mkisofs Compatibility<br> mkisofs implements version 1.09 of the Rock Ridge Interchange Protocol, whereas the
     * current version 1.12 was adopted as an IEEE standard. The differences are as follows:<br> 1. The ER System Use
     * Entry strings differ<br> 2. The PX System Use Entry does not contain a serial number in v1.09<br> 3. The RR
     * System Use Entry does not exist in v1.12
     *
     * @param flag Whether to use mkisofs compatibility or not
     */
    public void setMkisofsCompatibility(boolean flag) {
        RRIPFactory.MKISOFS_COMPATIBILITY = flag;
    }

    /**
     * Set maximum length of directory names
     *
     * @param length Maxiumum amount of characters
     *
     * @throws ConfigException Invalid length
     */
    public void setMaxDirectoryLength(int length) throws ConfigException {
        if (length < 0) {
            throw new ConfigException(this, "Invalid maximum directory length: " + length);
        }
        this.maxDirectoryLength = length;
    }

    /**
     * Set maximum length of file names
     *
     * @param length Maximum amount of characters
     *
     * @throws com.morpheusdata.javaisotools.iso9660.ConfigException Invalid length
     */
    public void setMaxFilenameLength(int length) throws ConfigException {
        if (length < 0) {
            throw new ConfigException(this, "Invalid maximum directory length: " + length);
        }
        this.maxFilenameLength = length;
    }

    /**
     * Force Portable Filename Character Set
     *
     * @param flag Whether to replace all characters outside the Portable Filename Character Set
     */
    public void forcePortableFilenameCharacterSet(boolean flag) {
        this.forcePortableFilenameCharacterSet = flag;
    }

    /**
     * Hide directory containing relocated directories
     *
     * @param flag Whether to prefix rr_moved with a dot
     */
    public void hideMovedDirectoriesStore(boolean flag) {
        this.hideMovedDirectoriesStore = flag;
    }

    /**
     * Add a new mode for a specific file pattern.
     * @param pattern the pattern to be matched
     * @param mode the POSIX file mode for matching filenames
     */
    public void addModeForPattern(String pattern, Integer mode) {
        log.info("*** Recording pattern \"{}\" with mode {}", pattern, String.format("%o", mode));
        patternToModeMap.put(pattern, mode);
    }

    /**
     * Retrieve the pattern-to-mode map in an unmodifiable form.
     * @return the pattern-to-mode map in an unmodifiable form.
     */
    public Map<String, Integer> getPatternToModeMap() {
        return Collections.unmodifiableMap(patternToModeMap);
    }
}
