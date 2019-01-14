package data;

public class Version implements Comparable<Version> {

    protected String releaseDate = "";
    private String versionNumber = "";
    private String versionURL = "";
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Version() {
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionURL() {
        return versionURL;
    }

    public void setVersionURL(String versionURL) {
        this.versionURL = versionURL;
    }

    @Override
    public int compareTo(Version o) {
        
        if (o == this)
            return 0;
        
        if (o == null)
            return 1;

        String o_versionStr = o.getVersionNumber();
        String this_versionStr = this.getVersionNumber();

        int compareResult = compareStr(this_versionStr, o_versionStr);
        
        return compareResult;
    }

    public static int compareStr(String firstVersion, String secondVersion) {
        String[] o_version = secondVersion.split("\\.");
        String[] this_version = firstVersion.split("\\.");

        int compareResult = 0;
        for (int i = 0; i < 4; i++) {

            Integer o_part = parseWithDefault(o_version[i], -1);
            Integer this_part = parseWithDefault(this_version[i], -1);

            if (o_part == -1 || this_part == -1) {
                compareResult = this_version[i].compareTo(o_version[i]);
            } else {
                compareResult = this_part.compareTo(o_part);
            }

            if (compareResult != 0) {
                break;
            }
        }
        return compareResult;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null)
            return false;
        
        if (!obj.getClass().equals(this.getClass()))
            return false;
        
        if (obj == this)
            return true;
                
        return this.getVersionNumber().equals(((Version)obj).getVersionNumber());
    }

    private static Integer parseWithDefault(String number, Integer defaultVal) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return String.format("[%s] -> (%s)", getVersionNumber(), getReleaseDate());
    }
}
