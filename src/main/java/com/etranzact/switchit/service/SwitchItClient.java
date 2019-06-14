package com.etranzact.switchit.service;

import com.etranzact.switchit.actions.AccountQuery;
import com.etranzact.switchit.actions.FundsTransfer;
import com.etranzact.switchit.actions.PayBills;
import com.etranzact.switchit.actions.TopUp;
import com.etranzact.switchit.helper.BankList;
import com.etranzact.switchit.response.SwitchResponse;
import com.etranzact.switchit.utils.ConnectionUtils;
import com.etranzact.switchit.utils.XMLUtils;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * @author joel.eze
 */
public class SwitchItClient {

    private final String environment;
    private final String terminalId;
    private final String encryptedPin;

    private static SwitchItClient instance;

    private static Logger l = Logger.getLogger(SwitchItClient.class);

    private XMLUtils xMLUtils = new XMLUtils();

    private SwitchItClient(String environment, String terminalId, String encryptedPin) {
        this.environment = environment;
        this.terminalId = terminalId;
        this.encryptedPin = encryptedPin;
    }

    public static SwitchItClient newInstance(String environment, String terminalId, String encryptedPin) {
        if (instance == null) {
            instance = new SwitchItClient(environment, terminalId, encryptedPin);
        }
        l.info("ROUTING TRANSACTIONS to " + environment + " with termainl ID " + terminalId);
        return instance;
    }

    public SwitchResponse queryAccount(AccountQuery aQ) throws Exception {
        String accountNumber = aQ.getAccountNumber();
        String reference = aQ.getReference();
        String bankCode = aQ.getBankCode();
        l.info("ACCOUNT NUMBER " + accountNumber);
        l.info("BANK " + bankCode);

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>AQ</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "               <bankCode>" + bankCode + "</bankCode>\n"
                + "               <destination>" + accountNumber + "</destination>\n"
                + "               <reference>" + reference + "</reference>\n"
                + "               <endPoint>A</endPoint>\n"
                + "               <terminalCard>false</terminalCard>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";


        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);
        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse sendMoney(FundsTransfer ft) throws Exception {
        String identifier;
        String endpoint;
        if (ft.isCardTransfer()) {
            identifier = ft.getCardNumber();
            endpoint = "C";
            l.info("Funds Transfer to Account: " + identifier);
        } else {
            identifier = ft.getAccountNumber();
            endpoint = "A";
            l.info("Funds Transfer to Card: " + identifier);
        }

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>FT</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "               <bankCode>" + ft.getBankCode() + "</bankCode>\n"
                + "               <currency>NGN</currency>\n"
                + "               <amount>" + ft.getAmount() + "</amount>\n"
                + "               <description>" + ft.getDescription() + "</description>\n"
                + "               <destination>" + identifier + "</destination>\n"
                + "               <reference>" + ft.getReference() + "</reference>\n"
                + "               <endPoint>" + endpoint + "</endPoint>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);
        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse balanceQueryOnTerminalId() throws Exception {

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>BE</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "               <reference>" + System.currentTimeMillis() + "</reference>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);
        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse topUp(TopUp tu) throws Exception {

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>VT</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "                <terminalCard>false</terminalCard>\n"
                + "               <senderName>" + tu.getSenderName() + "</senderName>\n"
                + "               <amount>" + tu.getAmount() + "</amount>\n"
                + "               <description>" + tu.getDescription() + "</description>\n"
                + "               <destination>" + tu.getDestinationPhoneNumber() + "</destination>\n"
                + "               <reference>" + tu.getReference() + "</reference>\n"
                + "               <lineType>VTU</lineType>\n"
                + "               <provider>" + tu.getProvider() + "</provider>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);
        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse checkTransactionStatus(String reference) throws Exception {

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>TS</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "                <terminalCard>false</terminalCard>\n"
                + "               <amount>0.0</amount>\n"
                + "               <reference>" + reference + "</reference>\n"
                + "               <lineType>OTHERS</lineType>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);
        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse verifyBillers(PayBills payBills) {

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>PB</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <id>1</id>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "                <lineType>" + payBills.getLineType() + "</lineType>\n"
                + "               <destination>" + payBills.getServiceId() + "</destination>\n"
                + "                <reference>VBILLER" + System.currentTimeMillis() + "</reference>\n"
                + "                  <senderName>" + payBills.getSenderName() + "</senderName>\n"
                + "                  <address>" + payBills.getDescription() + "</address>\n"
                + "               <endPoint>0</endPoint>\n"
                + "              <terminalCard>false</terminalCard>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);

        String responseMsg = xMLUtils.getXMLValue("message").replaceAll("&lt;", "<").replaceAll("&gt;", ">");

        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), responseMsg, xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse payBills(PayBills payBills) throws Exception {
        String xmlPayload;
        if (payBills.getLineType().contains("PHCN")) {
            xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <ws:process>\n"
                    + "         <request>\n"
                    + "            <direction>request</direction>\n"
                    + "            <action>PB</action>\n"
                    + "            <terminalId>" + terminalId + "</terminalId>\n"
                    + "            <transaction>\n"
                    + "            <id>2</id>\n"
                    + "              <senderName>" + payBills.getSenderName() + "</senderName>\n"
                    + "             <address>" + payBills.getDescription() + "</address>\n"
                    + "               <pin>" + encryptedPin + "</pin>\n"
                    + "               <amount>" + payBills.getAmount() + "</amount>\n"
                    + "                <lineType>" + payBills.getLineType() + "</lineType>\n"
                    + "               <destination>" + payBills.getServiceId() + "</destination>\n"
                    + "                <reference>" + payBills.getReference() + "</reference>\n"
                    + "              <terminalCard>false</terminalCard>\n"
                    + "            </transaction>\n"
                    + "         </request>\n"
                    + "      </ws:process>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
        } else {


            xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <ws:process>\n"
                    + "         <request>\n"
                    + "            <direction>request</direction>\n"
                    + "            <action>PB</action>\n"
                    + "            <terminalId>" + terminalId + "</terminalId>\n"
                    + "            <transaction>\n"
                    + "            <id>2</id>\n"
                    + "              <senderName>" + payBills.getSenderName() + "</senderName>\n"
                    + "             <address>" + payBills.getDescription() + "</address>\n"
                    + "               <pin>" + encryptedPin + "</pin>\n"
                    + "               <amount>" + payBills.getAmount() + "</amount>\n"
                    + "                <lineType>" + payBills.getLineType() + "</lineType>\n"
                    + "               <destination>" + payBills.getServiceId() + "</destination>\n"
                    + "                <reference>" + payBills.getReference() + "</reference>\n"
                    + "               <endPoint>0</endPoint>\n"
                    + "              <terminalCard>false</terminalCard>\n"
                    + "            </transaction>\n"
                    + "         </request>\n"
                    + "      </ws:process>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

        }
        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);

        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), xMLUtils.getXMLValue("message"), xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

    public SwitchResponse getBankList() throws Exception {

        String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.fundgate.etranzact.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ws:process>\n"
                + "         <request>\n"
                + "            <direction>request</direction>\n"
                + "            <action>BL</action>\n"
                + "            <terminalId>" + terminalId + "</terminalId>\n"
                + "            <transaction>\n"
                + "               <pin>" + encryptedPin + "</pin>\n"
                + "                <terminalCard>false</terminalCard>\n"
                + "               <amount>0.0</amount>\n"
                + "               <reference>ETZBL" + System.currentTimeMillis() + "</reference>\n"
                + "               <lineType>OTHERS</lineType>\n"
                + "            </transaction>\n"
                + "         </request>\n"
                + "      </ws:process>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        String response = new ConnectionUtils().sendPost(xmlPayload, environment);
        xMLUtils = new XMLUtils(response);

        String msg = xMLUtils.getXMLValue("message");

        System.out.println("MMSA " + msg);

        if (xMLUtils.getXMLValue("error").equals("0")) {

            JAXBContext jaxbContext = JAXBContext.newInstance(BankList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(msg.replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
            BankList bL = (BankList) unmarshaller.unmarshal(reader);

            GsonBuilder gsonBuilder = new GsonBuilder();
            msg = gsonBuilder.create().toJson(bL, BankList.class);
            l.info("Formated Response " + msg);
        }

        return new SwitchResponse(xMLUtils.getXMLValue("reference"), Double.parseDouble(xMLUtils.getXMLValue("amount")), xMLUtils.getXMLValue("totalFailed"), xMLUtils.getXMLValue("totalSuccess"), xMLUtils.getXMLValue("error"), msg, xMLUtils.getXMLValue("otherReference"), xMLUtils.getXMLValue("action"), xMLUtils.getXMLValue("openingBalance"), xMLUtils.getXMLValue("closingBalance"));

    }

//    public static void main(String[] args) {
//        SwitchItClient switchItClient = SwitchItClient.newInstance(ServiceURL.STAGING, "7000000001", "kghxqwveJ3eSQJip/cmaMQ==");
////        AccountQuery aQ = new AccountQuery();
////        aQ.setAccountNumber("2077939278");
////        aQ.setBankCode("033");
////        aQ.setReference("1212121d112121212");
////        try {
////            switchItClient.queryAccount(aQ);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        FundsTransfer ft = new FundsTransfer();
//        ft.setAccountNumber("2077939278");
//        ft.setAmount(100.0);
//        ft.setBankCode("033");
//        ft.setDescription("Transfer to Account");
//        ft.setReference("32322329uijkkknbk32");
//        ft.setSenderName("Joel");
//        ft.setIsCardTransfer(false);
//        try {
//            SwitchResponse response = switchItClient.sendMoney(ft);
//            System.out.println("DDFDfd " + response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        try {
////            System.out.println(switchItClient.getBankList());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
//    }
}
