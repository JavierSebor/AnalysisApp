package com.mundiaurum.analysisapp.domain;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

public class MAurumItem {
    private final String idPieza;
    private String idRecogida;
    private String strPiezaMetal;
    private String strPiezaMetalReal;
    private String strPiezaPureza;
    private String intPiezaPurezaReal;
    private String intPiezaPeso;
    private String intPiezaPesoReal;
    private String intPiezaPrecioFinal;
    private String intPiezaPrecioCalculadoInd;
    private String intPiezaPrecioCalculadoGrupo;
    private String intPiezaProcesoTerminado;
    private String intPiezaProcesoGrupoTerminado;
    private String strPiezaGrupo;
    private String strPiezaTipo;
    private String strPiezaDescripcionReal;
    private String strPiezaFoto1;
    private String strPiezaFoto2;
    private String dateFechaRecogida;

    private List<AnalysisFile> analysisFileList;
    public MAurumItem(String idPieza, String idRecogida, String strPiezaMetal, String strPiezaMetalReal,
                      String strPiezaPureza, String intPiezaPurezaReal, String intPiezaPeso, String intPiezaPesoReal,
                      String intPiezaPrecioFinal, String intPiezaPrecioCalculadoInd, String intPiezaPrecioCalculadoGrupo,
                      String intPiezaProcesoTerminado, String intPiezaProcesoGrupoTerminado, String strPiezaGrupo,
                      String strPiezaTipo, String strPiezaDescripcionReal, String strPiezaFoto1, String strPiezaFoto2, String dateFechaRecogida) {
        super();
        this.idPieza = idPieza;
        this.idRecogida = idRecogida;
        this.strPiezaMetal = strPiezaMetal;
        this.strPiezaMetalReal = strPiezaMetalReal;
        this.strPiezaPureza = strPiezaPureza;
        this.intPiezaPurezaReal = intPiezaPurezaReal;
        this.intPiezaPeso = intPiezaPeso;
        this.intPiezaPesoReal = intPiezaPesoReal;
        this.intPiezaPrecioFinal = intPiezaPrecioFinal;
        this.intPiezaPrecioCalculadoInd = intPiezaPrecioCalculadoInd;
        this.intPiezaPrecioCalculadoGrupo = intPiezaPrecioCalculadoGrupo;
        this.intPiezaProcesoTerminado = intPiezaProcesoTerminado;
        this.intPiezaProcesoGrupoTerminado = intPiezaProcesoGrupoTerminado;
        this.strPiezaGrupo = strPiezaGrupo;
        this.strPiezaTipo = strPiezaTipo;
        this.strPiezaDescripcionReal = strPiezaDescripcionReal;
        this.strPiezaFoto1 = strPiezaFoto1;
        this.strPiezaFoto2 = strPiezaFoto2;
        this.dateFechaRecogida = dateFechaRecogida;
        analysisFileList = new ArrayList<>();

    }


    public String getStrPiezaMetalReal() {
        return strPiezaMetalReal;
    }
    public void setStrPiezaMetalReal(String strPiezaMetalReal) {
        this.strPiezaMetalReal = strPiezaMetalReal;
    }
    public String getIntPiezaPurezaReal() {
        return intPiezaPurezaReal;
    }
    public void setIntPiezaPurezaReal(String intPiezaPurezaReal) {
        this.intPiezaPurezaReal = intPiezaPurezaReal;
    }
    public String getIntPiezaPesoReal() {
        return intPiezaPesoReal;
    }
    public void setIntPiezaPesoReal(String intPiezaPesoReal) {
        this.intPiezaPesoReal = intPiezaPesoReal;
    }
    public String getIntPiezaPrecioFinal() {
        return intPiezaPrecioFinal;
    }
    public void setIntPiezaPrecioFinal(String intPiezaPrecioFinal) {
        this.intPiezaPrecioFinal = intPiezaPrecioFinal;
    }
    public String getIntPiezaPrecioCalculadoInd() {
        return intPiezaPrecioCalculadoInd;
    }
    public void setIntPiezaPrecioCalculadoInd(String intPiezaPrecioCalculadoInd) {
        this.intPiezaPrecioCalculadoInd = intPiezaPrecioCalculadoInd;
    }
    public String getIntPiezaProcesoTerminado() {
        return intPiezaProcesoTerminado;
    }
    public void setIntPiezaProcesoTerminado(String intPiezaProcesoTerminado) {
        this.intPiezaProcesoTerminado = intPiezaProcesoTerminado;
    }
    public String getStrPiezaGrupo() {
        return strPiezaGrupo;
    }
    public void setStrPiezaGrupo(String strPiezaGrupo) {
        this.strPiezaGrupo = strPiezaGrupo;
    }
    public String getStrPiezaDescripcionReal() {
        return strPiezaDescripcionReal;
    }
    public void setStrPiezaDescripcionReal(String strPiezaDescripcionReal) {
        this.strPiezaDescripcionReal = strPiezaDescripcionReal;
    }
    public String getIdPieza() {
        return idPieza;
    }
    public String getIdRecogida() {
        return idRecogida;
    }
    public String getStrPiezaMetal() {
        return strPiezaMetal;
    }
    public String getStrPiezaPureza() {
        return strPiezaPureza;
    }
    public String getIntPiezaPeso() {
        return intPiezaPeso;
    }
    public String getIntPiezaPrecioCalculadoGrupo() {
        return intPiezaPrecioCalculadoGrupo;
    }
    public String getIntPiezaProcesoGrupoTerminado() {
        return intPiezaProcesoGrupoTerminado;
    }
    public String getStrPiezaTipo() {
        return strPiezaTipo;
    }
    public String getStrPiezaFoto1() {
        return strPiezaFoto1;
    }
    public String getStrPiezaFoto2() {
        return strPiezaFoto2;
    }
    public String getDateFechaRecogida() { return dateFechaRecogida; }

    public List<AnalysisFile> getAnalysisFileList() {
        return analysisFileList;
    }

    public void setAnalysisFileList(List<AnalysisFile> analysisFileList) {
        this.analysisFileList = analysisFileList;
    }

}
