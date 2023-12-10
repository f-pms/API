/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
 * Copyright (c) 2013,2016 Davide Nardella Copyright (c) 2017 J.Zimmermann (comtel2000)
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 *
 * Contributors: Davide Nardella - initial API and implementation J.Zimmermann - Mokka7 fork
 *
 */
package com.hbc.pms.plc.integration.mokka7.block;

import com.hbc.pms.plc.integration.mokka7.util.S7;

/**
 * See §33.19 of "System Software for S7-300/400 System and Standard Functions"
 *
 * @author Davide
 *
 */
public class S7Protection {

    /**
     * Startup switch setting (1:CRST, 2:WRST, 0:undefined, does not exist of cannot be determined)
     */
    public int anl_sch;

    /**
     * Mode selector setting (1:RUN, 2:RUN-P, 3:STOP, 4:MRES, 0:undefined or cannot be determined)
     */
    public int bart_sch;

    /**
     * Protection level set in parameters (0, 1, 2, 3; 0: no password, protection level invalid)
     */
    public int sch_par;

    /** Valid protection level of the CPU */
    public int sch_rel;

    /** Protection level set with the mode selector (1, 2, 3) */
    public int sch_schal;

    public static S7Protection of(byte[] src) {
        S7Protection pro = new S7Protection();
        pro.decode(src);
        return pro;
    }

    protected void decode(byte[] src) {
        sch_schal = S7.getWordAt(src, 2);
        sch_par = S7.getWordAt(src, 4);
        sch_rel = S7.getWordAt(src, 6);
        bart_sch = S7.getWordAt(src, 8);
        anl_sch = S7.getWordAt(src, 10);
    }

    @Override
    public String toString() {
        return "S7Protection [anl_sch=" + anl_sch + ", bart_sch=" + bart_sch + ", sch_par=" + sch_par + ", sch_rel=" + sch_rel + ", sch_schal=" + sch_schal
                + "]";
    }
}
