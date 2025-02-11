package butvinm.lab0.dolphins;

import butvinm.lab0.CommunicationProtocol;

class DolphinsCommunicationProtocol implements CommunicationProtocol {
    public String encode(String message) {
        return message + "!";
    }
}
