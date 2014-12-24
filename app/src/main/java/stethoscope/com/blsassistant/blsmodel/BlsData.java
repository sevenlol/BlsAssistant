package stethoscope.com.blsassistant.blsmodel;

public class BlsData{
    private String title;
    private String[] url, description, displayOrder;
    private boolean isRepChecked = true;

    //Constructor
    //TODO: Rep Exposure, fix later depends on performance
    public BlsData(String title, String[] url, String[] description, String[] displayOrder){
        this.title = title;
        this.url = url;
        this.description = description;
        this.displayOrder = displayOrder;

        //check if RI holds
        isRepChecked = checkRep();
    }

    public String getTitle(){
        isRepChecked = checkRep();
        return title;
    }

    public String[] getUrl(){
        isRepChecked = checkRep();
        return url;
    }

    public String[] getDescription(){
        isRepChecked = checkRep();
        return description;
    }

    public String[] getDisplayOrder(){
        isRepChecked = checkRep();
        return displayOrder;
    }

    public boolean isRepChecked(){
        isRepChecked = checkRep();
        return isRepChecked;
    }

    private boolean checkRep(){
        if (!isRepChecked)
            return false;

        if (title == null || url == null || description == null || displayOrder == null)
            return false;

        return url.length + description.length == displayOrder.length;
    }

}