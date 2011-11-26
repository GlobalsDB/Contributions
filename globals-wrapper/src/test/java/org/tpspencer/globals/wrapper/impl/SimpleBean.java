/**
 * Copyright (C) 2011 Tom Spencer <thegaffer@tpspencer.com>
 *
 * globals-wrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * globals-wrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with globals-wrapper. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tpspencer.globals.wrapper.impl;

import org.tpspencer.globals.wrapper.StorageDefinition;

/**
 * This is a simple object that is used in the mapping tests
 *
 * @author Tom Spencer
 */
public class SimpleBean {

    private String name;
    private long accountNos;
    private double balance;
    private String notes;
    
    @StorageDefinition(subNode=false, position=0)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @StorageDefinition(subNode=false, position=3)
    public long getAccountNos() {
        return accountNos;
    }
    
    public void setAccountNos(long accountNos) {
        this.accountNos = accountNos;
    }
    
    @StorageDefinition(subNode=false, position=5)
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    @StorageDefinition(subNode=true)
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
