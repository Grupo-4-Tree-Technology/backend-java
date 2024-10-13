package br.com.technology.tree.leituraPlanilha;

public class Acidente {
    private Integer id;
    private String data;
    private String horario;
    private String uf;
    private String municipio;
    private String causa;
    private String fase_dia;
    private String condicao_metereologica;
    private Integer quantidade_veiculos;

    public Acidente() {

    }

    public Acidente(Integer id, String data, String horario, String uf, String municipio, String causa, String fase_dia, String condicao_metereologica, Integer quantidade_veiculos) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.uf = uf;
        this.municipio = municipio;
        this.causa = causa;
        this.fase_dia = fase_dia;
        this.condicao_metereologica = condicao_metereologica;
        this.quantidade_veiculos = quantidade_veiculos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getFase_dia() {
        return fase_dia;
    }

    public void setFase_dia(String fase_dia) {
        this.fase_dia = fase_dia;
    }

    public String getCondicao_metereologica() {
        return condicao_metereologica;
    }

    public void setCondicao_metereologica(String condicao_metereologica) {
        this.condicao_metereologica = condicao_metereologica;
    }

    public Integer getQuantidade_veiculos() {
        return quantidade_veiculos;
    }

    public void setQuantidade_veiculos(Integer quantidade_veiculos) {
        this.quantidade_veiculos = quantidade_veiculos;
    }

    @Override
    public String toString() {
        return "Acidente{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", horario='" + horario + '\'' +
                ", uf='" + uf + '\'' +
                ", municipio='" + municipio + '\'' +
                ", causa='" + causa + '\'' +
                ", fase_dia='" + fase_dia + '\'' +
                ", condicao_metereologica='" + condicao_metereologica + '\'' +
                ", quantidade_veiculos=" + quantidade_veiculos +
                "}";
    }
}
