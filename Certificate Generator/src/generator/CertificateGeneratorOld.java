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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CertificateGeneratorOld {
    private static final String FONT = "c:/windows/fonts/arlrdbd.ttf";
    private static final String IMAGE = "resources/certificate-template-xix-1.jpg";//felipe assinatura
    private static final String IMAGE2 = "resources/certificate-template-xix-2.jpg";//jussara assinatura
    private static final String DEST = "results/";
    //text1 + nome + text2 + curso + text3 + horas + text4
    private static final String text1 = "Certificamos que ";
    private static final String text2_0 = " participou da oficina: ";
    private static final String text2_1 = " ministrou a oficina: ";
    private static final String text2_2 = " participou do evento";
    private static final String text2_3 = " ministrou a palestra: ";
    private static final String text2_4 = " participou como monitor";
    private static final String text2_5 = " participou da organização";
    private static final String text2_6 = " participou do workshop: ";
    private static final String text2_7 = " ministrou o workshop: ";
    private static final String text3 = ", com a carga horária de ";
    private static final String text4 = " horas, na XIX Semana de Integração de Engenharia da Computação, " +
                                        "realizada na Universidade Estadual de Feira de Santana " +
                                        "de 19 a 23 de Março de 2018.";
    
 
    public static void main(String args[]) throws IOException, DocumentException {
        HashMap<String,String> ordenador = new HashMap<>();
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
                            switch(aux[1].trim()){
                                case "oficina":
                                    tipo = 0;
                                    curso = "oficinas/"+StringUtils.capitalize(aux[2].trim());
                                    horas = aux [3].trim();
                                    break;
                                case "workshop":
                                    tipo = 6;
                                    curso = "workshops/"+StringUtils.capitalize(aux[2].trim());
                                    horas = aux [3].trim();
                                    break;
                                case "palestra":
                                    tipo = 2;
                                    curso = StringUtils.capitalize(aux[2].trim());
                                    horas = aux [3].trim();
                                    break;
                                case "monitor":
                                    tipo = 4;
                                    curso = "monitores";
                                    horas = aux [2].trim();
                                    break;
                                case "monitores":
                                    tipo = 4;
                                    curso = "monitores";
                                    horas = aux [2].trim();
                                    break;
                                case "organização":
                                    tipo = 5;
                                    curso = "organização";
                                    horas = aux [2].trim();
                                    break;
                                case "organizacao":
                                    tipo = 5;
                                    curso = "organização";
                                    horas = aux [2].trim();
                                    break;
                                default:
                                    tipo = 0;
                                    curso = "erro";
                                    horas = "00";
                                    break;
                            }                            
                        }else{
                            nome = line.split("/")[1].trim();
                            nomeAux = nome.split("\\s+");                            
                            nome = "";
                            for (String nomeAux1 : nomeAux) {
                                nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                            }
                            nome = nome.trim();
                            if(tipo==0)//oficina
                                tipo = 1;
                            else if (tipo==2)//palestra
                                tipo = 3;
                            else if (tipo==6)//workshop
                                tipo = 7;
                            createCertificate(curso, horas, nome, tipo);
                        }
                    }else {
                        nomeAux = line.split("\\s+");                            
                        nome = "";
                        for (String nomeAux1 : nomeAux) {
                            nome = (nomeAux1.length()>3)?nome.concat(StringUtils.capitalize(nomeAux1))+" ":nome.concat(nomeAux1)+" ";   
                        }
                        nome = nome.trim();
                        //ordenador.put(nome, nome);
                        
                        if(tipo==1)
                            tipo = 0;
                        else if (tipo==3)
                            tipo = 2;
                        else if (tipo==7)
                            tipo = 6;
                        createCertificate(curso, horas, nome, tipo);
                    }
                }       
            }
        }
        System.out.println("concluido!");
        /* //ordenar por ordem alfabetica
        List<String> listname = new ArrayList<String>(ordenador.keySet());
        Collections.sort(listname);
        for(String sd:listname){
            System.out.println(sd);
        }*/
        
    }
    public static void createCertificate(String curso, String horas, String nome, int tipo) throws DocumentException, IOException{        
        String dest;
        switch (tipo){
            case 3:
                dest = DEST+"palestras/palestrantes/"+curso+"/"+nome+".pdf";//palestrante
                break;
            case 2:
                dest = DEST+"palestras/"+nome+".pdf";//participação no evento
                break;
            default:
                dest = DEST+curso+"/"+nome+".pdf";//organizador
                break;
        }
        BaseFont bf = BaseFont.createFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font;        
        
        File file = new File(dest);
        file.getParentFile().mkdirs();
        Document document = new Document(PageSize.A4.rotate());
        //document.setMargins(80, 80, 380, 100); //A3  
        document.setMargins(50, 50, 270, 30); //A4
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
        if (curso.contains("/")){
            curso = curso.split("/")[1];
        }
        document.open();
        //font = new Font(FontFamily.TIMES_ROMAN,30);
        //font = new Font(bf,28);//A3
        //Paragraph paragrafo = new Paragraph(34,text1+nome+text2+horas+text3, font);//A3
        font = new Font(bf,20);//A4
        String text2 =null;
        if (curso.contains("+"))
            curso = curso.replace('+', '?');
        switch (tipo){
            case 0:
                text2 = text2_0+curso;//oficina ouvinte
                break;
            case 1:
                text2 = text2_1+curso;//oficina palestrante
                break;
            case 2:
                text2 = text2_2;//participação no evento
                break;
            case 3:
                text2 = text2_3+curso;//palestrante
                horas = "04";
                break;
            case 4:
                text2 = text2_4;//monitor
                break;
            case 5:
                text2 = text2_5;//organizador
                break;
            case 6:
                text2 = text2_6+curso;//workshop ouvinte
                break;
            case 7:
                text2 = text2_7+curso;//workshop palestrante
                break;
            default:
        }      
        
        Paragraph paragrafo = new Paragraph(26,text1+nome+text2+text3+horas+text4, font);//A3
        paragrafo.setAlignment(Element.ALIGN_CENTER);
        document.add(paragrafo);
        PdfContentByte canvas = writer.getDirectContentUnder();
        
        Image image;
        if (nome.toLowerCase().equals("felipe leonardo furtado barros"))
            image = Image.getInstance(IMAGE2);
        else
            image = Image.getInstance(IMAGE);
        image.scaleToFit(PageSize.A3.getWidth(), PageSize.A3.getHeight());
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
        document.close();
    }
    
}