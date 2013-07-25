/**
 * Author: Cody Boppert
 *
 * Fix4.4 New Order Single Message Parser
 *
 * Parses a Fix4.4 New Order Single Message and reports errors. */

/** The parser class takes command line arguments (a Fix 4.4 new order single
 * message) and parses it and reports whether any of the requirements are
 * missing from the message. */

/** The Fix4.4 protocal requires the following
 *
 * First (Required): 8=Fix4.4
 * Second (Required): 9=body length (Includes everything but 8=)
 * Third (Required): 35=MsgType
 *
 * Other required fields (unencrypted)
 * 49=SenderCompID
 * 56=TargetCompID
 *
 * Other required fields (possibly encrypted)
 * 34=MsgSeqNum
 * 52=SendingTime
 *
 * StandardTrailer CheckSum (must be last)
 * 19=CheckSum
 *
 */

/**
 * The FixParser class ensures the first, second, and third headers are
 * first, second and third within the message. Then it checks whether
 * the body length is correct. Finally it searches for the unencrypted
 * required fields but not the possibly encrypted fields as it has not
 * been specified as to what encryption might be used and what encoding
 * would have to be detected (base64 or hexadeximal?).
 * Finally it checks whether the check sum is present but because we do
 * not know the protocol of transmission does not have a reliable method 
 * for checking whether the check sum is correct. It does ensure the
 * checksum header is the final header within the message. */

public class FixParser {

    private String[] initialHeaders = {"8=FIX.4.49=", "35="};
    private String[] required = {"49=", "56="};
    private String checkSum = "10=";

    private void parser (String fixMsg) {
        if (fixMsg.substring(0, 11).equals(initialHeaders[0])) {
            // Cut off 8= to check body length
            fixMsg = fixMsg.substring(2);
            
            // Check if body length is correct
            String len = Integer.toString(fixMsg.length() - 1);
            if (fixMsg.substring(9, 9 + len.length()).equals(len.toString()) && (9 + len.length() < fixMsg.length())) {
                // Check 35= comes next (MsgType)
                if (fixMsg.substring(9 + len.length(), 12 + len.length()).equals(initialHeaders[1])) {
                    // Check for check sum before other required headers
                    int index = fixMsg.length() - 1;
                    while (fixMsg.charAt(index) != '=') {
                        index--;
                    }
                    if (fixMsg.substring(index - 2, index + 1).equals(checkSum) && index + 1 < fixMsg.length()) {
                        boolean checkSumBool = false;
                        try {
                            Integer.parseInt(fixMsg.substring(index + 1));
                            checkSumBool = true;
                        } catch (NumberFormatException e) {
                        } // end check for checkSum is Integer
                        if (checkSumBool) {
                            // Check for other required nonencrypted headers
                            int ind = 12 + len.length();
                            boolean[] checks = {false, false};
                            while (ind + 3 < index && (checks[0] == false || checks[1] == false)) {
                                if (fixMsg.substring(ind, ind + 3).equals(required[0])) {
                                    checks[0] = true;
                                }  
                                if (fixMsg.substring(ind, ind + 3).equals(required[1])) {
                                    checks[1] = true;
                                }
                                ind++;
                            } //end while (checks for required headers)

                            if (checks[0] && checks[1]) {
                                System.out.println("No Errors Detected.");
                            } else {
                                if (checks[0] != checks[1]) {
                                    if (checks[0]) {
                                        System.out.println("SenderCompID not found.");
                                    } else {
                                        System.out.println("TargetCompID not found.");
                                    }
                                } else {
                                    System.out.println("Neither SenderCompID nor TargetCompID found.");
                                }
                            } // End check for additional required headers
                        } else {
                            System.out.println("CheckSum Error. CheckSum is not integer.");
                        } // End check for CheckSum after determing checkSum header
                    } else {
                        System.out.println("CheckSum Error. CheckSum '10=' not detected.");
                    } // End check for CheckSum header
                } else {
                    System.out.println("Msg Type not present.");
                } // End check for MsgType
            } else {
                System.out.println("Body Length incorrect.");
            } // End check for body length
        } else {
            System.out.println("Initial Header is incorrect.");
        } // End check for initial 8=FIX4.49=

    }

    /** The main method may take multiple messages. */

    public static void main (String[] args) {
        for (String s: args) {
            FixParser fixParser = new FixParser();
            fixParser.parser(s);
        }
    }
}
