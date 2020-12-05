package com.neta.teman.dawai.api.models.reports;

import lombok.Data;

@Data
public class MasterCol {

    String col1, col2, col3, col4, col5, col6, col7, col8;

    public MasterCol(String col1, String col2, String col3) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
    }

    public MasterCol(String col1, String col2, String col3, String col4) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
    }

    public MasterCol(String col1, String col2, String col3, String col4, String col5) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
    }

    public MasterCol(String col1, String col2, String col3, String col4, String col5, String col6, String col7) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
        this.col6 = col6;
        this.col7 = col7;
    }

    public MasterCol(String col1, String col2, String col3, String col4, String col5, String col6, String col7, String col8) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
        this.col6 = col6;
        this.col7 = col7;
        this.col8 = col8;
    }
}
