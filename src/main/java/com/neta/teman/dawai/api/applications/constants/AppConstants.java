package com.neta.teman.dawai.api.applications.constants;

import java.io.File;

public class AppConstants {

    public static class Contact {
        public final static int PHONE = 1;
        public final static int EMAIL = 2;
    }

    public class Cuti {
        public static final int CANCEL = 1;
        public static final int REJECTED = 2;
        public static final int CREATED = 3;
    }

    public class Page {
        public static final int SHOW_IN_PAGE = 20;
    }

    public static String reportTemplate = "reports" + File.separator + "template" + File.separator;
    public static String reportExport = "reports" + File.separator + "export" + File.separator;

    public class Source {
        public final static String TEMAN_DAWAI = "TEMAN_DAWAI";
        public final static String SIMPEG = "SIMPEG";
    }
}
