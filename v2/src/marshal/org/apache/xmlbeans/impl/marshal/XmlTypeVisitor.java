/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.XmlException;

import javax.xml.namespace.QName;

abstract class XmlTypeVisitor
{
    private final Object parentObject;
    private final RuntimeBindingProperty bindingProperty;
    protected final MarshalResult marshalResult;

    XmlTypeVisitor(Object parentObject,
                   RuntimeBindingProperty property,
                   MarshalResult result)
    {
        this.parentObject = parentObject;
        this.bindingProperty = property;
        marshalResult = result;
    }


    protected Object getParentObject()
    {
        return parentObject;
    }

    protected RuntimeBindingProperty getBindingProperty()
    {
        return bindingProperty;
    }


    static final int START = 1;
    static final int CONTENT = 2;
    static final int CHARS = 3;
    static final int END = 4;

    protected abstract int getState();

    /**
     *
     * @return  next state
     */
    protected abstract int advance()
        throws XmlException;

    public abstract XmlTypeVisitor getCurrentChild()
        throws XmlException;


    protected abstract QName getName();

    //guaranteed to be called before any getAttribute* or getNamespace* method
    protected void initAttributes()
        throws XmlException
    {
    }

    protected abstract int getAttributeCount() 
        throws XmlException;

    protected abstract String getAttributeValue(int idx);

    protected abstract QName getAttributeName(int idx);

    protected abstract CharSequence getCharData();

    public String toString()
    {
        return this.getClass().getName() +
            " prop=" + bindingProperty.getName() +
            " type=" + bindingProperty.getType().getName();
    }

    protected QName fillPrefix(final QName pname)
    {
        final String uri = pname.getNamespaceURI();

        assert uri != null;  //QName's should use "" for no namespace

        if (uri.length() == 0) {
            return new QName(pname.getLocalPart());
        } else {
            String prefix = marshalResult.ensurePrefix(uri);
            return new QName(uri, pname.getLocalPart(), prefix);
        }
    }


}
