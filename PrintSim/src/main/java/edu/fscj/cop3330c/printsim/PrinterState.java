// PrinterState.java
// D. Singletary
// 11/17/24
// enforce printer state behavior

package edu.fscj.cop3330c.printsim;

interface PrinterState {
    void processQueue(Printer printer);
}