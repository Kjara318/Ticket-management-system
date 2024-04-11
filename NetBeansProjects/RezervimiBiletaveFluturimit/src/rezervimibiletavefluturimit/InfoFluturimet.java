package rezervimibiletavefluturimit;

//Ne klasen InfoFluturimet do te mbahen te gjitha te dhenat qe lidhen vetem me fluturimet pa
//te dhenat qe mund te plotesoje klienti/klientja.
public class InfoFluturimet {
    
    //Deklarimi i variablave te fushes behet privat ne menyre qe keto te dhena te mos aksesohen 
    //nga klasa te tjera(per arsye sigurie)
    private Integer fluturimiId;
    private String numriFluturimit;
    private String vendiNisjes;
    private String vendiMberritjes;
    private String oraENisjes;
    private String oraEMberritjes;
    private Integer avioniId;
    private Integer cmimeEkonomik;
    private Integer cmimeFirst;

    public InfoFluturimet(Integer fluturimiId, String numriFluturimit, String vendiNisjes, String vendiMberritjes,
            String oraENisjes, String oraEMberritjes, Integer avioniId, Integer cmimeEkonomik, Integer cmimeFirst) {
        this.fluturimiId = fluturimiId;
        this.numriFluturimit = numriFluturimit;
        this.vendiNisjes = vendiNisjes;
        this.vendiMberritjes = vendiMberritjes;
        this.oraENisjes = oraENisjes;
        this.oraEMberritjes = oraEMberritjes;
        this.avioniId = avioniId;
        this.cmimeEkonomik = cmimeEkonomik;
        this.cmimeFirst = cmimeFirst;
    }

    //Perdorim metoden get ne menyre qe te mundemi te aksesojme te dhena qe mund te na duhen 
    //edhe nga klasat e tjera
    public Integer getFluturimiId() {
        return fluturimiId;
    }

    public String getNumriFluturimit() {
        return numriFluturimit;
    }

    public String getVendiNisjes() {
        return vendiNisjes;
    }

    public String getVendiMberritjes() {
        return vendiMberritjes;
    }

    public String getOraENisjes() {
        return oraENisjes;
    }

    public String getOraEMberritjes() {
        return oraEMberritjes;
    }

    public Integer getAvioniId() {
        return avioniId;
    }

    public Integer getCmimeEkonomik() {
        return cmimeEkonomik;
    }

    public Integer getCmimeFirst() {
        return cmimeFirst;
    }
}