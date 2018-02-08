package za.co.barclays.strmap;

public class FingerprintEntry {
    private int min_strength;
    private int max_strength;
    private String mac_address;


    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public int getMax_strength() {
        return max_strength;
    }

    public void setMax_strength(int max_strength) {
        this.max_strength = max_strength;
    }

    public int getMin_strength() {
        return min_strength;
    }

    public void setMin_strength(int min_strength) {
        this.min_strength = min_strength;
    }

    public float scoreMac(int str){

        float avg = (this.getMin_strength()+this.getMax_strength())/2;
        float range = Math.abs(this.getMax_strength() - this.getMin_strength());
        range *= 1.2;

        float score = 0;

        if ((str <= avg-(range/2)) || (str >= avg+(range/2))){

            score = 0;

        }
        else if (str>=this.getMin_strength() && str<=this.getMax_strength() && str != avg){

            score = 1-(Math.abs(str-avg)/(range/2));

        }
        else if (str == avg){
            score = 1;
        }

        return score;

    }
}

