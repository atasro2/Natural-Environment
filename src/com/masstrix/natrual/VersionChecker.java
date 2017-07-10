package com.masstrix.natrual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    private int resourceId;
    private String latest = null, current;

    VersionChecker(int resourceId) {
        this.resourceId = resourceId;
        this.current = NaturalEnvironment.getInstance().getDescription().getVersion();
    }

    public String getCurrent() {
        return current;
    }

    public String getLatest() {
        if (latest != null) return latest;
        try {
            Logger.info("\u00A7aChecking if plugin is up to date...");
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("" + "resource=" + resourceId).getBytes("UTF-8"));
            latest = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        } catch (IOException e) {
            Logger.warn("\u00A7bUnable to check plugin latest!");
            e.printStackTrace();
        }
        return latest;
    }

    public boolean isRunningLatest() {
        return latest.equals(current);
    }

    public boolean isDevBuild() {
        return false;
    }
}
