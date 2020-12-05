package com.neta.teman.dawai.api.models.reports;

import lombok.Data;

@Data
public class Profile {

    String num;

    String title;

    String value;

    public Profile() {
    }

    public Profile(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public Profile(String num, String title, String value) {
        this.num = num;
        this.title = title;
        this.value = value;
    }
}
