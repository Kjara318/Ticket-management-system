package rezervimibiletavefluturimit;
import java.util.Date;

//Klasa do te mbaje te gjitha te dhenat qe klienti do te plotesoje per te rezervuar 
//fluturimin e tij/saj.
public class TeDhenatKlientit {
    
   //Bejme deklarimin e variablave te fushes per te gjitha te dhenat qe plotesohen nga 
   //perdoruesi. Variablat i deklarojme private per te ruajtur privatesine e klientit/klientes
    
    private String emri;            //mban emrin e klientit/klientes
    private String mbiemri;         //mban mbiemrin e klientit/klientes
    private String gjinia;          //mban gjinine e klientit/klientes
    private String nrtel;           //mban numrin e telefonit te klientit/klientes
    private String bagazhe;         //mban nese klienti/klientja ka bagazhe ose jo
    private Date dataN;             // mban daten
    private String vendiNisjes;         //mban vendin e nisjes
    private String vendiMberritjes;     //mban vendin e mberritjes
    private String klasi;               //mban klasin ne te cilin udheton klienti/klientja
    private Integer idBileta;           //mban id e biletes
    private Integer nrBiletave;         //mban nr e biletes qe ka klienti/klientja
    private Integer totali;             //mban cmimin e biletes ne varesi te zgjedhjeve qe 
                                        //ben klienti/klientja per udhetimin e tij/saj
    
    //Krojojme konstruktorin qe do te mbaje te gjitha keto te dhena
    public TeDhenatKlientit(String emri, String mbiemri, String gjinia, String nrtel, String bagazhe,Date dataN, String vendiNisjes, String vendiMberritjes,String klasi,Integer idBileta, Integer nrBiletave, Integer totali){
        this.nrBiletave = nrBiletave;
        this.emri = emri;
        this.mbiemri = mbiemri;
        this.gjinia = gjinia;
        this.nrtel = nrtel;
        this.bagazhe = bagazhe;
        this.dataN = dataN;
        this.vendiNisjes = vendiNisjes;
        this.vendiMberritjes = vendiMberritjes;
        this.klasi= klasi;
        this.idBileta = idBileta;
        this.nrBiletave = nrBiletave;
        this.totali = totali;
    }

    TeDhenatKlientit() {
    }
    
    //Perdorim metoden get per secilen nga te dhenat ne menyre qe te aksesojme 
    //keto te dhena edhe nga klasat e tjera
    public String getEmri(){
        return emri;
    }
     public String getMbiemri(){
        return mbiemri;
    }
      public String getGjinia(){
        return gjinia;
    }
       public String getNrTel(){
        return nrtel;
    }
        public String getBagazhe(){
        return bagazhe;
    }
        public Date getData(){
        return dataN;
    }
        public String getVendiNisjes(){
        return vendiNisjes;
    }
        public String getVendiMberritjes(){
        return vendiMberritjes;
    }
        public String getKlasi(){
        return klasi;
    }
        public Integer getidBileta(){
        return idBileta;
    }
    
        public Integer getnrBileta(){
        return nrBiletave;
    }
        
        public Integer getTotali(){
        return totali;
    }      
}