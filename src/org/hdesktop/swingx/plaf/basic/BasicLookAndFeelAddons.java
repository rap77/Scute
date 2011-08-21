/*
 * $Id: BasicLookAndFeelAddons.java 3672 2010-04-25 23:11:19Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
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
package org.hdesktop.swingx.plaf.basic;

import org.hdesktop.swingx.plaf.LookAndFeelAddons;
import org.hdesktop.swingx.plaf.UIManagerExt;

/**
 * Install simple pluggable UI. Usually not used directly, subclasses should be
 * preferred as this addon may not provide complete implementation of the
 * additional pluggable UIs.
 */
public class BasicLookAndFeelAddons extends LookAndFeelAddons {
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        super.initialize();
        //must add resource bundle after adding component values
        UIManagerExt.addResourceBundle(
                "org.hdesktop.swingx.plaf.basic.resources.swingx");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninitialize() {
        //must remove resource bundle before adding component values
        UIManagerExt.removeResourceBundle(
                "org.hdesktop.swingx.plaf.basic.resources.swingx");
        super.uninitialize();
    }
    
}
