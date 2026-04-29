package com.cruvex.cubepanionleaderboardsubmitter.managers;

public class CubeCraftManager {
    private static CubeCraftManager instance;

    String serverIp;

    String serverID;
    String lastServerID;

    private CubeCraftManager() {}

    public static CubeCraftManager getInstance() {
        if (instance == null) {
            instance = new CubeCraftManager();

            instance.serverIp = "";
            instance.serverID = "";
            instance.lastServerID = "";
        }
        return instance;
    }

    public void reset() {
        this.serverIp = "";
        this.serverID = "";
        this.lastServerID = "";
    }

    public void onCubeJoin() {
        this.serverIp = "play.cubecraft.net";
    }

    public boolean onCubeCraft() {
        return this.serverIp.equals("play.cubecraft.net");
    }

    public void setServerID(String serverID) {
        this.lastServerID = this.serverID;
        this.serverID = serverID;
    }

    public String getServerID() {
        return this.serverID;
    }

    public String getLastServerID() {
        return this.lastServerID;
    }
}
