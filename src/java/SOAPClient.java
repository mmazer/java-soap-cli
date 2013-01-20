import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;


public class SOAPClient {

    public static final String USER_AGENT = "SOAPClient/1.0";

    public static final String SOAP_CONTENT_TYPE = "text/xml; charset=utf-8";

    public static final String SOAP_ACTION_HEADER = "SOAPAction";

    public SOAPClient() {
        super();
    }

    public static void main(String[] args) throws Exception {

        Options options = getOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cl = parser.parse(options, args);


        if(cl.hasOption('h')) {
            printUsage(options);
        }

        if(!cl.hasOption('u')) {
            throw new MissingArgumentException("no web service URL specified");
        }
        String endpoint = cl.getOptionValue('u');

        if(!cl.hasOption('f')) {
            throw new MissingArgumentException("no file for SOAP request specified");
        }
        File file = new File(cl.getOptionValue('f'));

        URL url = new URL(endpoint);

        PostMethod post = new PostMethod(url.toExternalForm());

        post.setRequestEntity(new FileRequestEntity(file, SOAP_CONTENT_TYPE));
        post.setContentChunked(true);

        if (cl.hasOption('U')) {
            String userAgent = cl.getOptionValue('U');
            if (userAgent != null) {
                post.setRequestHeader("User-Agent", userAgent);
            }
        }

        String soapAction = "";
        if(cl.hasOption('a')) {
            soapAction = cl.getOptionValue('a');
            post.setRequestHeader(SOAP_ACTION_HEADER, soapAction);
        }
        if (cl.hasOption('H')) {
            Properties headers = cl.getOptionProperties("H");
            for (String name : headers.stringPropertyNames()) {
                post.setRequestHeader(name, headers.getProperty(name));
            }

        }
        HttpClient httpClient = new HttpClient();
        if (cl.hasOption('e')) {
            httpClient.getParams().setParameter("http.protocol.expect-continue", new Boolean(true));
        }
        int status = httpClient.executeMethod(post);

        System.out.println(post.getStatusLine());
        Header[] headers = post.getResponseHeaders();
        for (Header h: headers) {
            System.out.print(h.toExternalForm());
        }
        System.out.println();
        System.out.println(post.getResponseBodyAsString());


    }

    private static Options getOptions() {

        Options options = new Options();
        options.addOption(new Option("a", "soap-action", true, "SOAP action"));
        options.addOption(new Option("e", "expect-continue", false, "HTTP Expect-Continue"));
        options.addOption(new Option("f", "soap-request", true, "SOAP request file"));
        options.addOption(new Option("H", "header", true, "HTTP header"));
        options.addOption( OptionBuilder.withArgName( "header=value" )
                                         .hasArgs(2)
                                         .withValueSeparator()
                                         .withDescription( "HTTP header value" )
                                         .create( "H" ));
        options.addOption(new Option("h", "help", false,"show help information"));
        options.addOption(new Option("u", "url", true, "web service endpoint URL"));
        options.addOption(new Option("U", "agent", true, "user agent"));

        return options;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "SOAPClient", options );
        System.exit(0);
    }
}
