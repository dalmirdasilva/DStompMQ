/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.br.dstompmqui;

import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dalmir
 */
public class ConnectionTableModel extends DefaultTableModel {
    
    Class[] types = new Class[]{
        java.lang.String.class, java.lang.String.class, java.lang.String.class, Date.class, java.lang.Float.class, java.lang.Float.class, java.lang.Boolean.class
    };
    
    public ConnectionTableModel(String[] names) {
        super(names, 0);
    }
    
    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int columnIndex, int i) {
        return false;
    }
}

