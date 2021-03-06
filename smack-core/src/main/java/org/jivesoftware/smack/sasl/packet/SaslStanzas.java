/**
 *
 * Copyright 2014 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.sasl.packet;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class SaslStanzas {
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    /**
     * Initiating SASL authentication by select a mechanism.
     */
    public static class AuthMechanism extends Packet {
        public static final String ELEMENT = "auth";

        private final String mechanism;
        private final String authenticationText;

        public AuthMechanism(String mechanism, String authenticationText) {
            if (mechanism == null) {
                throw new NullPointerException("SASL mechanism shouldn't be null.");
            }
            if (StringUtils.isNullOrEmpty(authenticationText)) {
                throw new IllegalArgumentException("SASL authenticationText must not be null or empty (RFC6120 6.4.2)");
            }
            this.mechanism = mechanism;
            this.authenticationText = authenticationText;
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).attribute("mechanism", mechanism).rightAngelBracket();
            xml.optAppend(authenticationText);
            xml.closeElement(ELEMENT);
            return xml;
        }
    }

    /**
     * A SASL challenge stanza.
     */
    public static class Challenge extends Packet {
        public static final String ELEMENT = "challenge";

        private final String data;

        public Challenge(String data) {
            this.data = StringUtils.returnIfNotEmptyTrimmed(data);
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder().halfOpenElement(ELEMENT).xmlnsAttribute(
                            NAMESPACE).rightAngelBracket();
            xml.optAppend(data);
            xml.closeElement(ELEMENT);
            return xml;
        }
    }

    /**
     * A SASL response stanza.
     */
    public static class Response extends Packet {
        public static final String ELEMENT = "response";

        private final String authenticationText;

        public Response() {
            authenticationText = null;
        }

        public Response(String authenticationText) {
            this.authenticationText = StringUtils.returnIfNotEmptyTrimmed(authenticationText);
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).rightAngelBracket();
            xml.optAppend(authenticationText);
            xml.closeElement(ELEMENT);
            return xml;
        }
    }

    /**
     * A SASL success stanza.
     */
    public static class Success extends Packet {
        public static final String ELEMENT = "success";

        final private String data;

        /**
         * Construct a new SASL success stanza with optional additional data for the SASL layer
         * (RFC6120 6.3.10)
         * 
         * @param data additional data for the SASL layer or <code>null</code>
         */
        public Success(String data) {
            this.data = StringUtils.returnIfNotEmptyTrimmed(data);
        }

        /**
         * Returns additional data for the SASL layer or <code>null</code>.
         * 
         * @return additional data or <code>null</code>
         */
        public String getData() {
            return data;
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE).rightAngelBracket();
            xml.optAppend(data);
            xml.closeElement(ELEMENT);
            return xml;
        }
    }

    /**
     * A SASL failure stanza.
     */
    public static class SASLFailure extends Packet {
        public static final String ELEMENT = "failure";

        private final SASLError saslError;
        private final String saslErrorString;

        public SASLFailure(String saslError) {
            SASLError error = SASLError.fromString(saslError);
            if (error == null) {
                // RFC6120 6.5 states that unknown condition must be treat as generic authentication
                // failure.
                this.saslError = SASLError.not_authorized;
            }
            else {
                this.saslError = error;
            }
            this.saslErrorString = saslError;
        }

        /**
         * Get the SASL related error condition.
         * 
         * @return the SASL related error condition.
         */
        public SASLError getSASLError() {
            return saslError;
        }

        /**
         * @return the SASL error as String
         */
        public String getSASLErrorString() {
            return saslErrorString;
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(ELEMENT).xmlnsAttribute(ELEMENT).rightAngelBracket();
            xml.emptyElement(saslErrorString);
            xml.closeElement(ELEMENT);
            return xml;
        }
    }
}
