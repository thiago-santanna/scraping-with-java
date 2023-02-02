import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        String palavraChave = "java";
        String baseUrl = "https://pt.stackoverflow.com";
        String paginaProcurada = baseUrl+"/questions/tagged/" + URLEncoder.encode(palavraChave, StandardCharsets.UTF_8);

        HtmlPage page = client.getPage(paginaProcurada);

        List<Pergunta> perguntas = new ArrayList<>();
        List<HtmlElement> htmlElementList = page.getByXPath("//div[@class='s-post-summary--content']");
        if (!htmlElementList.isEmpty()) {
            for (HtmlElement item:htmlElementList) {
                HtmlAnchor itemAnchor = item.getFirstByXPath(".//h3[@class='s-post-summary--content-title']/a");
                String titulo = limparPuloLinha(itemAnchor.getFirstChild().asXml());
                String linkPergunta =  baseUrl + itemAnchor.getHrefAttribute();

                HtmlElement elementDiv = item.getFirstByXPath(".//div[@class='s-post-summary--content-excerpt']");
                String descricao = limparPuloLinha(elementDiv.getFirstChild().asXml());

                Pergunta pergunta = new Pergunta();
                pergunta.setTitulo(titulo);
                pergunta.setLinkPergunta(linkPergunta);
                pergunta.setDescricao(descricao);

                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(pergunta);
                System.out.println(jsonString);

                perguntas.add(pergunta);
            }

            System.out.println("");
            System.out.println("");
            System.out.println("Perguntas nesta lista");
            for (Pergunta pergunta: perguntas) {
                System.out.print(pergunta.getTitulo());
                System.out.print(" - ");
                System.out.println(pergunta.getLinkPergunta());
            }
        }else {
            System.out.println("Elementos nao encontrados");
        }
    }
    private static String limparPuloLinha(String texto){
        return texto.replaceAll("\n", " ")
            .replaceAll("\r", " ")
            .replaceAll("\r\n", " ")
            .trim();
    }
}