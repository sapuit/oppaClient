package vn.soaap.onlinepharmacy.util;

import java.io.File;

import vn.soaap.onlinepharmacy.app.Config;

/**
 * Created by sapui on 6/27/2016.
 */
public class FolderHelper {


    public static void makeAppFolderIfNotExist() {

        File appFolder = new File(Config.APP_FOLDER);
        if (!appFolder.exists())
            appFolder.mkdir();
        String nomediaFile = Config.APP_FOLDER + ".nomedia";

        File nomedia = new File(nomediaFile);
        if(!nomedia.exists()){
            try {
                nomedia.createNewFile();
//                final File file = new File(Config.APP_FOLDER, ".nomedia");
//                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
