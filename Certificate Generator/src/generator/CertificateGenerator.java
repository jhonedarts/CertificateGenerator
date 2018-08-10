/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertificateGenerator {
    private HashMap<String,ArrayList<String>> categorias = new HashMap<>();
    private String FONT = "c:/windows/fonts/arlrdbd.ttf";
    private String IMAGE = "src/view/template.jpg";
    private String DEST = "results/";
    //text1 + nome + text2 + curso + text3 + horas + text4
    private String text1 = "Certificamos que ";
    private ArrayList<ArrayList<String>> text2 = new ArrayList();
    
    private String text3 = ", com a carga horária de ";
    private String text4 = " horas, na ";
    private String eventoHoras = "20";
    
    public void salvar() throws FileNotFoundException, IOException{
        String t = "config.data";
        FileOutputStream f = new FileOutputStream(t);
        ObjectOutputStream ob = new ObjectOutputStream(f);
        ob.writeObject(text2);
        f.close();
        ob.close();
    }
    public boolean carregar() {
        String t = "config.data";
        FileInputStream f;
        try {
            f = new FileInputStream(t);
            ObjectInputStream ob=new ObjectInputStream(f);
            Object x = ob.readObject();
            f.close();
            ob.close();
            text2 = (ArrayList<ArrayList<String>>) x;
            String cat = null;
            for(int u=0;u<text2.size();u++){
                cat = text2.get(u).get(0);
                categorias.put(cat, text2.get(u));
            }
        } catch (IOException|ClassNotFoundException ex) {
            return false;
        } 
           //Verificar a exception que da quando nao acha o arquivo
        return true;
    }
    
    public void reset(){
        text2 = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        temp.add("final");
        temp.add("XIX Semana de Integração de Engenharia da Computação, realizada na Universidade Estadual de Feira de Santana de 19 a 23 de Março de 2018.");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("oficina");
        temp.add(" participou da oficina: ");
        temp.add(" ministrou a oficina: ");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("workshop");
        temp.add(" participou do workshop: ");
        temp.add(" ministrou o workshop: ");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("palestra");
        temp.add(" participou do evento ");
        temp.add(" ministrou a palestra: ");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("monitor");
        temp.add(" participou como monitor ");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("organizacao");
        temp.add(" participou da organização ");
        text2.add(temp);
        temp = new ArrayList<>();
        temp.add("imgPath");
        temp.add("src/view/template.jpg");
        text2.add(temp);
        try {
            salvar();
        } catch (IOException ex) {
            Logger.getLogger(CertificateGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() throws IOException, DocumentException {
        String cat = null;
        System.out.println("executando...");
        File folder = new File("in/");
        File[] listOfFiles = folder.listFiles();
        //leitor        
        String line, curso = null, nome, horas = null;
        String aux[], nomeAux[];
        int tipo = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(listOfFiles[i]), "UTF-8"));
            while ((line = reader.readLine()) != null){
                
                if(line.length()>3){
                    if(line.contains("/") ){
                        if (line.contains(";")){
                            aux = line.split("/|;");
                            
                            ArrayList<String> auxList = categorias.get(aux[1].trim());
                            if (auxList != null){
                                cat = auxList.get(0);
                                if(auxList.get(0).charAt(auxList.get(0).length()-1)=='r')
                                    curso = auxList.get(0)+"es";
                                else if (auxList.get(0).charAt(auxList.get(0).length()-1)=='s'||auxList.get(0).charAt(auxList.get(0).length()-1)=='o')
                                    curso = auxList.get(0);
                                else
                                    curso = auxList.get(0)+"s";
                                if(aux.length>3){
                                    if (curso.contains("palestra"))
                                        curso = "palestras/palestrantes/"+StringUtils.capitalize(aux[2].trim());
                                    else
                                        curso = curso+"/"+StringUtils.capitalize(aux[2].trim());
                                    horas = aux [3].trim();
                                }else
                                    horas = aux [2].trim();                                
                            }else{
                                cat = "erro";
                                curso = "erro";
                                horas = "00";
                            }                        
                        }else{
                            nome = line.split("/")[1].trim();
                            nomeAux = nome.split("\\s+");                            
                            nome = "";
                            for (String nomeAux1 : nomeAux) {
                                nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                            }
                            nome = nome.trim();
                            if(tipo==1)//muda de participante pra palestrante
                                tipo = 2;
                            createCertificate(cat,curso, horas, nome, tipo);
                        }
                    }else {
                        nomeAux = line.split("\\s+");                            
                        nome = "";
                        for (String nomeAux1 : nomeAux) {
                            nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                        }
                        nome = nome.trim();
                        
                        if(tipo==2)//muda de palestrante pra participante
                            tipo = 1;
                        if(curso.contains("palestra"))
                            curso = "palestras";
                        createCertificate(cat,curso, horas, nome, tipo);
                    }
                }       
            }
        }
        System.out.println("concluido!");
    }
    public void createCertificate(String cat, String curso, String horas, String nome, int tipo) throws DocumentException, IOException{        
        String dest = DEST+curso+"/"+nome+".pdf";//organizador
        String cursoParticipante = null;
        BaseFont bf = BaseFont.createFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font;        
        
        File file = new File(dest);
        file.getParentFile().mkdirs();
        Document document = new Document(PageSize.A4.rotate());
        //document.setMargins(80, 80, 380, 100); //A3  
        document.setMargins(50, 50, 270, 30); //A4
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
        
        if(curso.contains("palestra")&& !curso.contains("palestrante"))
            horas = eventoHoras;
        cursoParticipante = "";
        if (curso.contains("/")){
            curso = curso.split("/")[curso.split("/").length-1];
            if(curso.split("/").length==2)
                cursoParticipante = curso;
        }
            
        document.open();
        //font = new Font(FontFamily.TIMES_ROMAN,30);
        //font = new Font(bf,28);//A3
        //Paragraph paragrafo = new Paragraph(34,text1+nome+text2+horas+text3, font);//A3
        font = new Font(bf,20);//A4
        String text2 =null;
        if (curso.contains("+"))
            curso = curso.replace('+', '?');
        
        ArrayList<String> auxList = categorias.get(cat);
        if (tipo==1)//participante
            text2 = auxList.get(1)+cursoParticipante;
        else{
            String t = "";
            if(auxList!=null)
                t = (auxList.size()==2)?"":auxList.get(2);
            text2 = t+curso;
        }
        
        String text5 = categorias.get("final").get(1);
        Paragraph paragrafo = new Paragraph(26,text1+nome+text2+text3+horas+text4+text5, font);//A3
        paragrafo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragrafo);
        PdfContentByte canvas = writer.getDirectContentUnder();
        
        Image image = Image.getInstance(IMAGE);
        image.scaleToFit(PageSize.A3.getWidth(), PageSize.A3.getHeight());
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
        document.close();
    }
    
    public void ordenar() throws IOException{
        HashMap<String,String> ordenador = null;
        
        System.out.println("ordenando...");
        File folder = new File("in/");
        File[] listOfFiles = folder.listFiles();
        //leitor        
        boolean palestrante =false;
        String line, nome;
        String aux[], nomeAux[];
        for (int i = 0; i < listOfFiles.length; i++) {if(!listOfFiles[i].getName().contains("ORD.txt")){
            ordenador = new HashMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(listOfFiles[i]), "UTF-8"));
            String filename = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf('.'))+"ORD.txt";
            File file1 = new File(filename);       
            if (!file1.exists()){      
                new File(filename).createNewFile();
                file1 = new File(filename);
            }
            FileWriter arq = new FileWriter(file1);
            PrintWriter gravarArq = new PrintWriter(arq);
            while ((line = reader.readLine()) != null){
                if(line.length()>3){
                    if(line.contains("/") ){
                        if (line.contains(";")){
                            //zerar list
                            List<String> listname = new ArrayList<String>(ordenador.keySet());
                            Collections.sort(listname);
                            String df ="";
                            if(palestrante)
                                df="/";
                            
                            for(String sd:listname){
                                System.out.println(df+sd);
                                gravarArq.printf(df+sd+"%n");
                            }
                            gravarArq.printf("%n");
                            ordenador = new HashMap<>();
                            //escrever nome do curso em arquivo  
                            gravarArq.printf(line+"%n");
                        }else{
                            palestrante = true;
                            nome = line.split("/")[1].trim();
                            nomeAux = nome.split("\\s+");                            
                            nome = "";
                            for (String nomeAux1 : nomeAux) {
                                nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                            }
                            nome = nome.trim();
                            
                            ordenador.put(nome, nome);
                        }
                    }else {
                        if(palestrante){
                            List<String> listname = new ArrayList<String>(ordenador.keySet());
                            Collections.sort(listname);
                            for(String sd:listname){
                                System.out.println("/"+sd);
                                gravarArq.printf("/"+sd+"%n");
                            }
                            gravarArq.printf("%n");
                            ordenador = new HashMap<>();
                            palestrante = false;
                        }
                        nomeAux = line.split("\\s+");                            
                        nome = "";
                        for (String nomeAux1 : nomeAux) {
                            nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                        }
                        nome = nome.trim();
                        ordenador.put(nome, nome);
                    }
                }       
            }
            gravarArq.printf("%n");
            List<String> listname = new ArrayList<String>(ordenador.keySet());
            Collections.sort(listname);
            for(String sd:listname){
                //System.out.println(sd);
                gravarArq.printf(sd+"%n");//escrever em arquivo 
            }
            ordenador = null;
            palestrante = false;

            arq.close();
            
        }}
        
        System.out.println("concluido!");
    }

    public String getFONT() {
        return FONT;
    }

    public void setFONT(String FONT) {
        this.FONT = FONT;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
        String cat = null;
        for(int u=0;u<text2.size();u++){
            if(text2.get(u).get(0).equals("imgPath")){
                text2.get(u).set(1, this.IMAGE);
            }
        }
    }

    public String getDEST() {
        return DEST;
    }

    public void setDEST(String DEST) {
        this.DEST = DEST;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public ArrayList<ArrayList<String>> getText2() {
        return text2;
    }

    public void setText2(ArrayList<ArrayList<String>> text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getEventoHoras() {
        return eventoHoras;
    }

    public void setEventoHoras(String eventoHoras) {
        this.eventoHoras = eventoHoras;
    }

    public String getFinal() {
        return categorias.get("final").get(1);
    }
    
    
}