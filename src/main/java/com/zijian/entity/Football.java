package com.zijian.entity;

import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import javax.persistence.Entity;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SolrDocument(collection="Football")
public class Football implements Serializable{
    @Field
    @Id
    private String id;
    @Field("Domicile")
    private String domicile;
    @Field("Visiteur")
    private String visiteur;
    @Field("Note_Final")
    private String note_final;
    @Field("Note_Mi_Temps")
    private String note_mi_temps;
    @Field("Buts_Domicile")
    private Integer buts_domicile;
    @Field("Buts_Visiteur")
    private Integer buts_visiteur;
    @Field("Buts_Domicile_Mi_Temps")
    private Integer buts_domicile_mi_temps;
    @Field("Buts_Visiteur_Mi_Temps")
    private Integer buts_visiteurs_mi_temps;
    @Field("Domicile_Gagner_")
    private boolean domicile_gagner;
    @Field("Visiteur_Gagner_")
    private boolean visiteur_gagner;
    @Field("Jour")
    private String jour;
    @Field("Date")
    private String date;
    @Field("Saison")
    private String saison;
}
