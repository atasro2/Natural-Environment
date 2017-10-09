package com.masstrix.natrual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

public class VersionChecker {

    private String latest = "unknown", current = "unknown";

    VersionChecker(int resourceId) {
        this.current = NaturalEnvironment.getInstance().getDescription().getVersion();

        try {
            Logger.info("Checking versions...");
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setConnectTimeout(10000);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("" + "resource=" + resourceId).getBytes("UTF-8"));
            latest = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (isDevBuild()) Logger.info("&eYou are a running dev build of NaturalEnvironment. Expect bugs and errors.");
            else if (isRunningLatest()) Logger.info("&aUp to date.");
            else Logger.warn(
                    "You are running an out dated and unsupported version of NaturalEnvironment. " +
                        "Please update to the latest version for support and bug fix's.");
        } catch (IOException e) {
            if (e instanceof SocketException)
                Logger.info("Offline? No connection was able to be made to check versions.");
            else Logger.warn("Unable to check if plugins is up to date!");
        }
    }

    public String getCurrent() {
        return current;
    }

    public String getLatest() {
        return latest;
    }

    public boolean isRunningLatest() {
        return latest.equals(current);
    }

    public boolean isDevBuild() {
        String[] v1 = current.split("\\.");
        String[] v2 = latest.split("\\.");
        if (v1.length > v2.length) return true;
        for (int i = 0; i < v1.length; i++) {
            int vc = Integer.parseInt(v1[i]), vl = Integer.parseInt(v2[i]);
            if (vc > vl) return true;
        }
        return false;
    }
}
