/*
 * Copyright 2004-2022 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
*//**
 * MySwing: Advanced Swing Utilites
 * Copyright (C) 2005  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package skt.swing.tree;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Stack;

/**
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 */
public class PreorderEnumeration implements Enumeration {
    private TreeModel model;
    protected Stack stack = new Stack();

    public PreorderEnumeration(TreePath path, TreeModel model) {
        this(Collections.enumeration(Collections.singletonList(path)), model);
    }

    public PreorderEnumeration(Enumeration enumer, TreeModel model) {
        this.model = model;
        stack.push(enumer);
    }

    public boolean hasMoreElements() {
        return (!stack.empty() &&
                ((Enumeration) stack.peek()).hasMoreElements());
    }

    public Object nextElement() {
        Enumeration enumer = (Enumeration) stack.peek();
        TreePath path = (TreePath) enumer.nextElement();

        if (!enumer.hasMoreElements()) {
            stack.pop();
        }

        if (model.getChildCount(path.getLastPathComponent()) > 0) {
            stack.push(new ChildrenEnumeration(path, model));
        }
        return path;
    }
}
