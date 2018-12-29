/*
 *  Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.custom;

import org.apache.axis2.client.Options;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * In synapse configuration you have to specify full qualified class name as the name for the class mediator as follows.
 * <class name="org.wso2.carbon.custom.DisableSecMediator"/>
 */
public class DisableSecMediator extends AbstractMediator {

    /**
     * Mediate the message.
     * <p/>
     * This is the execution point of the mediator.
     *
     * @param synapseMsgContext MessageContext to be mediated
     * @return whether to continue the flow
     */
    public boolean mediate(MessageContext synapseMsgContext) {

        org.apache.axis2.context.MessageContext messageContext =
                ((Axis2MessageContext) synapseMsgContext).getAxis2MessageContext();

        InputStream stream = new ByteArrayInputStream(getPolicy().getBytes());
        Policy policy = PolicyEngine.getPolicy(stream);

        if (policy != null) {
            messageContext.setProperty("rampartOutPolicy", policy);

            Options options = messageContext.getOptions();
            if (options == null) {
                return true;
            }

            Options parentOptions = options.getParent();
            if (parentOptions == null) {
                return true;
            }

            Options grandParentOptions = parentOptions.getParent();
            if (grandParentOptions == null) {
                return true;
            }
            grandParentOptions.setProperty("rampartPolicy", null);

        }
        return true;
    }

    private String getPolicy() {

        return "<wsp:Policy wsu:Id=\"emptryPolicy\" xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\" " +
                "xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
                "<wsp:ExactlyOne><wsp:All><sp:TransportBinding xmlns:sp=\"http://schemas.xmlsoap.org/ws/2005/07/securitypolicy\">" +
                "<wsp:Policy></wsp:Policy></sp:TransportBinding></wsp:All></wsp:ExactlyOne></wsp:Policy>";
    }

}
