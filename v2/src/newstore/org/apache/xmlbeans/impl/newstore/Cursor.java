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

package org.apache.xmlbeans.impl.newstore;

import javax.xml.namespace.QName;

import javax.xml.stream.XMLStreamReader;

import org.apache.xmlbeans.xml.stream.XMLInputStream;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlDocumentProperties;

import java.util.Map;
import java.util.Collection;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;

import org.apache.xmlbeans.impl.newstore.pub.store.Cur;
import org.apache.xmlbeans.impl.newstore.pub.store.Locale;

public final class Cursor implements XmlCursor
{
    Cursor ( Cur c )
    {
        _locale = c.locale();
        _cur = c.weakCur( this );
    }

    private boolean isValid ( )
    {
        // TODO - make sure we're not in attr/pi/comment, etc ...
        
        return true;
    }

    public void dump ( )
    {
        _cur.dump();
    }

    //
    //
    //

    // TODO - deal with cursors moving to other documents upon release?
    // Can I move the ref from one q to another?  If not I will have to
    // change from a phantom ref to a soft/weak ref so I can know what
    // to do when I dequeue from the old q.
    
    public TokenType _currentTokenType ( )
    {
        switch ( _cur.type() )
        {
        case   Cur.ROOT : return TokenType.STARTDOC;
        case - Cur.ROOT : return TokenType.ENDDOC;
        case   Cur.ELEM : return TokenType.START;
        case - Cur.ELEM : return TokenType.END;
        case   Cur.TEXT : return TokenType.TEXT;
                           
        case Cur.ATTR :
        {
            assert _cur.kind() == Cur.ATTR || _cur.kind() == Cur.XMLNS;
            return _cur.kind() == Cur.ATTR ? TokenType.ATTR : TokenType.NAMESPACE;
        }
                      
        case Cur.LEAF :
        {
            assert _cur.kind() == Cur.COMMENT || _cur.kind() == Cur.PROCINST;
            return _cur.kind() == Cur.COMMENT ? TokenType.COMMENT : TokenType.PROCINST;
        }

        default :
            throw new IllegalStateException();
        }
    }

    public TokenType _toNextToken ( )
    {
        switch ( _cur.type() )
        {
        case Cur.ROOT :
        case Cur.ELEM :
        {
            if (!_cur.toFirstAttr())
                _cur.next();

            break;
        }
        
        case Cur.ATTR :
        {
            if (!_cur.toNextSibling())
            {
                _cur.toParent();
                _cur.next();
            }

            break;
        }

        case Cur.LEAF :
        {
            _cur.toEnd();
            _cur.next();

            break;
        }
        
        default :
            _cur.next();
            break;
        }

        return _currentTokenType();
    }

    public XmlCursor _newCursor ( )
    {
        return new Cursor( _cur );
    }

    public Object _monitor ( )
    {
        // TODO - some of these methods need not be protected by a
        // gatway.  This is one of them.  Inline this.

        return _locale;
    }
    
    public boolean _isStartdoc ( )
    {
        // TODO - can make these faster .... perhaps even inline?
        return _currentTokenType().isStartdoc();
    }

    public boolean _isEnddoc ( )
    {
        return _currentTokenType().isEnddoc();
    }

    public boolean _isStart ( )
    {
        return _currentTokenType().isStart();
    }

    public boolean _isEnd ( )
    {
        return _currentTokenType().isEnd();
    }

    public boolean _isText ( )
    {
        return _currentTokenType().isText();
    }

    public boolean _isAttr ( )
    {
        return _currentTokenType().isAttr();
    }

    public boolean _isNamespace ( )
    {
        return _currentTokenType().isNamespace();
    }

    public boolean _isComment ( )
    {
        return _currentTokenType().isComment();
    }

    public boolean _isProcinst ( )
    {
        return _currentTokenType().isProcinst();
    }

    public boolean _isContainer ( )
    {
        return _currentTokenType().isContainer();
    }

    public boolean _isFinish ( )
    {
        return _currentTokenType().isFinish();
    }

    public boolean _isAnyAttr ( )
    {
        return _currentTokenType().isAnyAttr();
    }

    public boolean _toFirstChild ( )
    {
        Cur c = _cur.tempCur();

        if (!c.toNearestContainer() || !c.toFirstChildElem())
            return false;

        _cur.moveToCur( c );

        c.release();

        return true;
    }

    public boolean _toParent ( )
    {
        Cur c = _cur.tempCur();

        if (!c.toParent())
            return false;

        _cur.moveToCur( c );

        c.release();

        return true;
    }





    

    public XmlDocumentProperties _documentProperties ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XMLStreamReader _newXMLStreamReader ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XMLStreamReader _newXMLStreamReader ( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }

    public XMLInputStream _newXMLInputStream ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _xmlText ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public InputStream _newInputStream ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public Reader _newReader ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public Node _newDomNode ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( ContentHandler ch, LexicalHandler lh ) throws SAXException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( File file ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( OutputStream os ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( Writer w ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XMLInputStream _newXMLInputStream ( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _xmlText ( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public InputStream _newInputStream ( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public Reader _newReader( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public Node _newDomNode ( XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( ContentHandler ch, LexicalHandler lh, XmlOptions options ) throws SAXException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( File file, XmlOptions options ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( OutputStream os, XmlOptions options ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _save ( Writer w, XmlOptions options ) throws IOException
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _dispose ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toCursor ( XmlCursor moveTo )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _push ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _pop ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _selectPath ( String path )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _selectPath ( String path, XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _hasNextSelection ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextSelection ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toSelection ( int i )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _getSelectionCount ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _addToSelection ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _clearSelections ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toBookmark ( XmlBookmark bookmark )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlBookmark _toNextBookmark ( Object key )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlBookmark _toPrevBookmark ( Object key )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public QName _getName ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _setName ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _namespaceForPrefix ( String prefix )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _prefixForNamespace ( String namespaceURI )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _getAllNamespaces ( Map addToThis )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlObject _getObject ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public TokenType _prevTokenType ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _hasNextToken ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _hasPrevToken ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public TokenType _toPrevToken ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public TokenType _toFirstContentToken ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public TokenType _toEndToken ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _toNextChar ( int maxCharacterCount )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _toPrevChar ( int maxCharacterCount )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextSibling ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toPrevSibling ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toLastChild ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toChild ( String name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toChild ( String namespace, String name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toChild ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toChild ( int index )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toChild ( QName name, int index )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextSibling ( String name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextSibling ( String namespace, String name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextSibling ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toFirstAttribute ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toLastAttribute ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toNextAttribute ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _toPrevAttribute ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _getAttributeText ( QName attrName )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _setAttributeText ( QName attrName, String value )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _removeAttribute ( QName attrName )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _getTextValue ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _getTextValue ( char[] returnedChars, int offset, int maxCharacterCount )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _setTextValue ( String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _setTextValue ( char[] sourceChars, int offset, int length )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public String _getChars ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _getChars ( char[] returnedChars, int offset, int maxCharacterCount )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _toStartDoc ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _toEndDoc ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _isInSameDocument ( XmlCursor cursor )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _comparePosition ( XmlCursor cursor )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _isLeftOf ( XmlCursor cursor )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _isAtSamePositionAs ( XmlCursor cursor )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _isRightOf ( XmlCursor cursor )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlCursor _execQuery ( String query )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlCursor _execQuery ( String query, XmlOptions options )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public ChangeStamp _getDocChangeStamp ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _setBookmark ( XmlBookmark bookmark )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public XmlBookmark _getBookmark ( Object key )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _clearBookmark ( Object key )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _getAllBookmarkRefs ( Collection listToFill )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _removeXml ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _moveXml ( XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _copyXml ( XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _removeXmlContents ( )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _moveXmlContents ( XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public boolean _copyXmlContents ( XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _removeChars ( int maxCharacterCount )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _moveChars ( int maxCharacterCount, XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public int _copyChars ( int maxCharacterCount, XmlCursor toHere )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertChars ( String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElement ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElement ( String localName )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElement ( String localName, String uri )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _beginElement ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _beginElement ( String localName )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _beginElement ( String localName, String uri )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElementWithText ( QName name, String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElementWithText ( String localName, String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertElementWithText ( String localName, String uri, String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttribute ( String localName )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttribute ( String localName, String uri )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttribute ( QName name )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttributeWithValue ( String Name, String value )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttributeWithValue ( String name, String uri, String value )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertAttributeWithValue ( QName name, String value )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertNamespace ( String prefix, String namespace )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertComment ( String text )
    {
        throw new RuntimeException( "Not implemented" );
    }
    
    public void _insertProcInst ( String target, String text )
    {
        throw new RuntimeException( "Not implemented" );
    }

    //
    //
    //

    public boolean toCursor ( XmlCursor moveTo ) { if (_locale.noSync()) { _locale.enter(); try { return _toCursor( moveTo ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toCursor( moveTo ); } finally { _locale.exit(); } } }
    public boolean isInSameDocument ( XmlCursor cursor ) { if (_locale.noSync()) { _locale.enter(); try { return _isInSameDocument( cursor ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isInSameDocument( cursor ); } finally { _locale.exit(); } } }
    
    public Object monitor ( ) { if (_locale.noSync()) { _locale.enter(); try { return _monitor(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _monitor(); } finally { _locale.exit(); } } }
    public XmlDocumentProperties documentProperties ( ) { if (_locale.noSync()) { _locale.enter(); try { return _documentProperties(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _documentProperties(); } finally { _locale.exit(); } } }
    public XmlCursor newCursor ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newCursor(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newCursor(); } finally { _locale.exit(); } } }
    public XMLStreamReader newXMLStreamReader ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newXMLStreamReader(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newXMLStreamReader(); } finally { _locale.exit(); } } }
    public XMLStreamReader newXMLStreamReader ( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _newXMLStreamReader( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newXMLStreamReader( options ); } finally { _locale.exit(); } } }
    public XMLInputStream newXMLInputStream ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newXMLInputStream(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newXMLInputStream(); } finally { _locale.exit(); } } }
    public String xmlText ( ) { if (_locale.noSync()) { _locale.enter(); try { return _xmlText(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _xmlText(); } finally { _locale.exit(); } } }
    public InputStream newInputStream ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newInputStream(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newInputStream(); } finally { _locale.exit(); } } }
    public Reader newReader ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newReader(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newReader(); } finally { _locale.exit(); } } }
    public Node newDomNode ( ) { if (_locale.noSync()) { _locale.enter(); try { return _newDomNode(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newDomNode(); } finally { _locale.exit(); } } }
    public void save ( ContentHandler ch, LexicalHandler lh ) throws SAXException { if (_locale.noSync()) { _locale.enter(); try { _save( ch, lh ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( ch, lh ); } finally { _locale.exit(); } } }
    public void save ( File file ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( file ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( file ); } finally { _locale.exit(); } } }
    public void save ( OutputStream os ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( os ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( os ); } finally { _locale.exit(); } } }
    public void save ( Writer w ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( w ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( w ); } finally { _locale.exit(); } } }
    public XMLInputStream newXMLInputStream ( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _newXMLInputStream( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newXMLInputStream( options ); } finally { _locale.exit(); } } }
    public String xmlText ( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _xmlText( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _xmlText( options ); } finally { _locale.exit(); } } }
    public InputStream newInputStream ( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _newInputStream( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newInputStream( options ); } finally { _locale.exit(); } } }
    public Reader newReader( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _newReader( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newReader( options ); } finally { _locale.exit(); } } }
    public Node newDomNode ( XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _newDomNode( options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _newDomNode( options ); } finally { _locale.exit(); } } }
    public void save ( ContentHandler ch, LexicalHandler lh, XmlOptions options ) throws SAXException { if (_locale.noSync()) { _locale.enter(); try { _save( ch, lh, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( ch, lh, options ); } finally { _locale.exit(); } } }
    public void save ( File file, XmlOptions options ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( file, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( file, options ); } finally { _locale.exit(); } } }
    public void save ( OutputStream os, XmlOptions options ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( os, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( os, options ); } finally { _locale.exit(); } } }
    public void save ( Writer w, XmlOptions options ) throws IOException { if (_locale.noSync()) { _locale.enter(); try { _save( w, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _save( w, options ); } finally { _locale.exit(); } } }
    public void dispose ( ) { if (_locale.noSync()) { _locale.enter(); try { _dispose(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _dispose(); } finally { _locale.exit(); } } }
    public void push ( ) { if (_locale.noSync()) { _locale.enter(); try { _push(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _push(); } finally { _locale.exit(); } } }
    public boolean pop ( ) { if (_locale.noSync()) { _locale.enter(); try { return _pop(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _pop(); } finally { _locale.exit(); } } }
    public void selectPath ( String path ) { if (_locale.noSync()) { _locale.enter(); try { _selectPath( path ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _selectPath( path ); } finally { _locale.exit(); } } }
    public void selectPath ( String path, XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { _selectPath( path, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _selectPath( path, options ); } finally { _locale.exit(); } } }
    public boolean hasNextSelection ( ) { if (_locale.noSync()) { _locale.enter(); try { return _hasNextSelection(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _hasNextSelection(); } finally { _locale.exit(); } } }
    public boolean toNextSelection ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextSelection(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextSelection(); } finally { _locale.exit(); } } }
    public boolean toSelection ( int i ) { if (_locale.noSync()) { _locale.enter(); try { return _toSelection( i ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toSelection( i ); } finally { _locale.exit(); } } }
    public int getSelectionCount ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getSelectionCount(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getSelectionCount(); } finally { _locale.exit(); } } }
    public void addToSelection ( ) { if (_locale.noSync()) { _locale.enter(); try { _addToSelection(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _addToSelection(); } finally { _locale.exit(); } } }
    public void clearSelections ( ) { if (_locale.noSync()) { _locale.enter(); try { _clearSelections(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _clearSelections(); } finally { _locale.exit(); } } }
    public boolean toBookmark ( XmlBookmark bookmark ) { if (_locale.noSync()) { _locale.enter(); try { return _toBookmark( bookmark ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toBookmark( bookmark ); } finally { _locale.exit(); } } }
    public XmlBookmark toNextBookmark ( Object key ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextBookmark( key ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextBookmark( key ); } finally { _locale.exit(); } } }
    public XmlBookmark toPrevBookmark ( Object key ) { if (_locale.noSync()) { _locale.enter(); try { return _toPrevBookmark( key ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toPrevBookmark( key ); } finally { _locale.exit(); } } }
    public QName getName ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getName(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getName(); } finally { _locale.exit(); } } }
    public void setName ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { _setName( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _setName( name ); } finally { _locale.exit(); } } }
    public String namespaceForPrefix ( String prefix ) { if (_locale.noSync()) { _locale.enter(); try { return _namespaceForPrefix( prefix ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _namespaceForPrefix( prefix ); } finally { _locale.exit(); } } }
    public String prefixForNamespace ( String namespaceURI ) { if (_locale.noSync()) { _locale.enter(); try { return _prefixForNamespace( namespaceURI ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _prefixForNamespace( namespaceURI ); } finally { _locale.exit(); } } }
    public void getAllNamespaces ( Map addToThis ) { if (_locale.noSync()) { _locale.enter(); try { _getAllNamespaces( addToThis ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _getAllNamespaces( addToThis ); } finally { _locale.exit(); } } }
    public XmlObject getObject ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getObject(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getObject(); } finally { _locale.exit(); } } }
    public TokenType currentTokenType ( ) { if (_locale.noSync()) { _locale.enter(); try { return _currentTokenType(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _currentTokenType(); } finally { _locale.exit(); } } }
    public boolean isStartdoc ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isStartdoc(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isStartdoc(); } finally { _locale.exit(); } } }
    public boolean isEnddoc ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isEnddoc(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isEnddoc(); } finally { _locale.exit(); } } }
    public boolean isStart ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isStart(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isStart(); } finally { _locale.exit(); } } }
    public boolean isEnd ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isEnd(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isEnd(); } finally { _locale.exit(); } } }
    public boolean isText ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isText(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isText(); } finally { _locale.exit(); } } }
    public boolean isAttr ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isAttr(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isAttr(); } finally { _locale.exit(); } } }
    public boolean isNamespace ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isNamespace(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isNamespace(); } finally { _locale.exit(); } } }
    public boolean isComment ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isComment(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isComment(); } finally { _locale.exit(); } } }
    public boolean isProcinst ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isProcinst(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isProcinst(); } finally { _locale.exit(); } } }
    public boolean isContainer ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isContainer(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isContainer(); } finally { _locale.exit(); } } }
    public boolean isFinish ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isFinish(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isFinish(); } finally { _locale.exit(); } } }
    public boolean isAnyAttr ( ) { if (_locale.noSync()) { _locale.enter(); try { return _isAnyAttr(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isAnyAttr(); } finally { _locale.exit(); } } }
    public TokenType prevTokenType ( ) { if (_locale.noSync()) { _locale.enter(); try { return _prevTokenType(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _prevTokenType(); } finally { _locale.exit(); } } }
    public boolean hasNextToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _hasNextToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _hasNextToken(); } finally { _locale.exit(); } } }
    public boolean hasPrevToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _hasPrevToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _hasPrevToken(); } finally { _locale.exit(); } } }
    public TokenType toNextToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextToken(); } finally { _locale.exit(); } } }
    public TokenType toPrevToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toPrevToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toPrevToken(); } finally { _locale.exit(); } } }
    public TokenType toFirstContentToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toFirstContentToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toFirstContentToken(); } finally { _locale.exit(); } } }
    public TokenType toEndToken ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toEndToken(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toEndToken(); } finally { _locale.exit(); } } }
    public int toNextChar ( int maxCharacterCount ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextChar( maxCharacterCount ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextChar( maxCharacterCount ); } finally { _locale.exit(); } } }
    public int toPrevChar ( int maxCharacterCount ) { if (_locale.noSync()) { _locale.enter(); try { return _toPrevChar( maxCharacterCount ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toPrevChar( maxCharacterCount ); } finally { _locale.exit(); } } }
    public boolean toNextSibling ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextSibling(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextSibling(); } finally { _locale.exit(); } } }
    public boolean toPrevSibling ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toPrevSibling(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toPrevSibling(); } finally { _locale.exit(); } } }
    public boolean toParent ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toParent(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toParent(); } finally { _locale.exit(); } } }
    public boolean toFirstChild ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toFirstChild(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toFirstChild(); } finally { _locale.exit(); } } }
    public boolean toLastChild ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toLastChild(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toLastChild(); } finally { _locale.exit(); } } }
    public boolean toChild ( String name ) { if (_locale.noSync()) { _locale.enter(); try { return _toChild( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toChild( name ); } finally { _locale.exit(); } } }
    public boolean toChild ( String namespace, String name ) { if (_locale.noSync()) { _locale.enter(); try { return _toChild( namespace, name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toChild( namespace, name ); } finally { _locale.exit(); } } }
    public boolean toChild ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { return _toChild( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toChild( name ); } finally { _locale.exit(); } } }
    public boolean toChild ( int index ) { if (_locale.noSync()) { _locale.enter(); try { return _toChild( index ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toChild( index ); } finally { _locale.exit(); } } }
    public boolean toChild ( QName name, int index ) { if (_locale.noSync()) { _locale.enter(); try { return _toChild( name, index ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toChild( name, index ); } finally { _locale.exit(); } } }
    public boolean toNextSibling ( String name ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextSibling( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextSibling( name ); } finally { _locale.exit(); } } }
    public boolean toNextSibling ( String namespace, String name ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextSibling( namespace, name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextSibling( namespace, name ); } finally { _locale.exit(); } } }
    public boolean toNextSibling ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextSibling( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextSibling( name ); } finally { _locale.exit(); } } }
    public boolean toFirstAttribute ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toFirstAttribute(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toFirstAttribute(); } finally { _locale.exit(); } } }
    public boolean toLastAttribute ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toLastAttribute(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toLastAttribute(); } finally { _locale.exit(); } } }
    public boolean toNextAttribute ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toNextAttribute(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toNextAttribute(); } finally { _locale.exit(); } } }
    public boolean toPrevAttribute ( ) { if (_locale.noSync()) { _locale.enter(); try { return _toPrevAttribute(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _toPrevAttribute(); } finally { _locale.exit(); } } }
    public String getAttributeText ( QName attrName ) { if (_locale.noSync()) { _locale.enter(); try { return _getAttributeText( attrName ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getAttributeText( attrName ); } finally { _locale.exit(); } } }
    public boolean setAttributeText ( QName attrName, String value ) { if (_locale.noSync()) { _locale.enter(); try { return _setAttributeText( attrName, value ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _setAttributeText( attrName, value ); } finally { _locale.exit(); } } }
    public boolean removeAttribute ( QName attrName ) { if (_locale.noSync()) { _locale.enter(); try { return _removeAttribute( attrName ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _removeAttribute( attrName ); } finally { _locale.exit(); } } }
    public String getTextValue ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getTextValue(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getTextValue(); } finally { _locale.exit(); } } }
    public int getTextValue ( char[] returnedChars, int offset, int maxCharacterCount ) { if (_locale.noSync()) { _locale.enter(); try { return _getTextValue( returnedChars, offset, maxCharacterCount ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getTextValue( returnedChars, offset, maxCharacterCount ); } finally { _locale.exit(); } } }
    public void setTextValue ( String text ) { if (_locale.noSync()) { _locale.enter(); try { _setTextValue( text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _setTextValue( text ); } finally { _locale.exit(); } } }
    public void setTextValue ( char[] sourceChars, int offset, int length ) { if (_locale.noSync()) { _locale.enter(); try { _setTextValue( sourceChars, offset, length ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _setTextValue( sourceChars, offset, length ); } finally { _locale.exit(); } } }
    public String getChars ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getChars(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getChars(); } finally { _locale.exit(); } } }
    public int getChars ( char[] returnedChars, int offset, int maxCharacterCount ) { if (_locale.noSync()) { _locale.enter(); try { return _getChars( returnedChars, offset, maxCharacterCount ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getChars( returnedChars, offset, maxCharacterCount ); } finally { _locale.exit(); } } }
    public void toStartDoc ( ) { if (_locale.noSync()) { _locale.enter(); try { _toStartDoc(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _toStartDoc(); } finally { _locale.exit(); } } }
    public void toEndDoc ( ) { if (_locale.noSync()) { _locale.enter(); try { _toEndDoc(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _toEndDoc(); } finally { _locale.exit(); } } }
    public int comparePosition ( XmlCursor cursor ) { if (_locale.noSync()) { _locale.enter(); try { return _comparePosition( cursor ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _comparePosition( cursor ); } finally { _locale.exit(); } } }
    public boolean isLeftOf ( XmlCursor cursor ) { if (_locale.noSync()) { _locale.enter(); try { return _isLeftOf( cursor ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isLeftOf( cursor ); } finally { _locale.exit(); } } }
    public boolean isAtSamePositionAs ( XmlCursor cursor ) { if (_locale.noSync()) { _locale.enter(); try { return _isAtSamePositionAs( cursor ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isAtSamePositionAs( cursor ); } finally { _locale.exit(); } } }
    public boolean isRightOf ( XmlCursor cursor ) { if (_locale.noSync()) { _locale.enter(); try { return _isRightOf( cursor ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _isRightOf( cursor ); } finally { _locale.exit(); } } }
    public XmlCursor execQuery ( String query ) { if (_locale.noSync()) { _locale.enter(); try { return _execQuery( query ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _execQuery( query ); } finally { _locale.exit(); } } }
    public XmlCursor execQuery ( String query, XmlOptions options ) { if (_locale.noSync()) { _locale.enter(); try { return _execQuery( query, options ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _execQuery( query, options ); } finally { _locale.exit(); } } }
    public ChangeStamp getDocChangeStamp ( ) { if (_locale.noSync()) { _locale.enter(); try { return _getDocChangeStamp(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getDocChangeStamp(); } finally { _locale.exit(); } } }
    public void setBookmark ( XmlBookmark bookmark ) { if (_locale.noSync()) { _locale.enter(); try { _setBookmark( bookmark ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _setBookmark( bookmark ); } finally { _locale.exit(); } } }
    public XmlBookmark getBookmark ( Object key ) { if (_locale.noSync()) { _locale.enter(); try { return _getBookmark( key ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _getBookmark( key ); } finally { _locale.exit(); } } }
    public void clearBookmark ( Object key ) { if (_locale.noSync()) { _locale.enter(); try { _clearBookmark( key ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _clearBookmark( key ); } finally { _locale.exit(); } } }
    public void getAllBookmarkRefs ( Collection listToFill ) { if (_locale.noSync()) { _locale.enter(); try { _getAllBookmarkRefs( listToFill ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _getAllBookmarkRefs( listToFill ); } finally { _locale.exit(); } } }
    public boolean removeXml ( ) { if (_locale.noSync()) { _locale.enter(); try { return _removeXml(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _removeXml(); } finally { _locale.exit(); } } }
    public boolean moveXml ( XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _moveXml( toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _moveXml( toHere ); } finally { _locale.exit(); } } }
    public boolean copyXml ( XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _copyXml( toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _copyXml( toHere ); } finally { _locale.exit(); } } }
    public boolean removeXmlContents ( ) { if (_locale.noSync()) { _locale.enter(); try { return _removeXmlContents(); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _removeXmlContents(); } finally { _locale.exit(); } } }
    public boolean moveXmlContents ( XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _moveXmlContents( toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _moveXmlContents( toHere ); } finally { _locale.exit(); } } }
    public boolean copyXmlContents ( XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _copyXmlContents( toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _copyXmlContents( toHere ); } finally { _locale.exit(); } } }
    public int removeChars ( int maxCharacterCount ) { if (_locale.noSync()) { _locale.enter(); try { return _removeChars( maxCharacterCount ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _removeChars( maxCharacterCount ); } finally { _locale.exit(); } } }
    public int moveChars ( int maxCharacterCount, XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _moveChars( maxCharacterCount, toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _moveChars( maxCharacterCount, toHere ); } finally { _locale.exit(); } } }
    public int copyChars ( int maxCharacterCount, XmlCursor toHere ) { if (_locale.noSync()) { _locale.enter(); try { return _copyChars( maxCharacterCount, toHere ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { return _copyChars( maxCharacterCount, toHere ); } finally { _locale.exit(); } } }
    public void insertChars ( String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertChars( text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertChars( text ); } finally { _locale.exit(); } } }
    public void insertElement ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { _insertElement( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElement( name ); } finally { _locale.exit(); } } }
    public void insertElement ( String localName ) { if (_locale.noSync()) { _locale.enter(); try { _insertElement( localName ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElement( localName ); } finally { _locale.exit(); } } }
    public void insertElement ( String localName, String uri ) { if (_locale.noSync()) { _locale.enter(); try { _insertElement( localName, uri ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElement( localName, uri ); } finally { _locale.exit(); } } }
    public void beginElement ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { _beginElement( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _beginElement( name ); } finally { _locale.exit(); } } }
    public void beginElement ( String localName ) { if (_locale.noSync()) { _locale.enter(); try { _beginElement( localName ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _beginElement( localName ); } finally { _locale.exit(); } } }
    public void beginElement ( String localName, String uri ) { if (_locale.noSync()) { _locale.enter(); try { _beginElement( localName, uri ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _beginElement( localName, uri ); } finally { _locale.exit(); } } }
    public void insertElementWithText ( QName name, String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertElementWithText( name, text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElementWithText( name, text ); } finally { _locale.exit(); } } }
    public void insertElementWithText ( String localName, String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertElementWithText( localName, text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElementWithText( localName, text ); } finally { _locale.exit(); } } }
    public void insertElementWithText ( String localName, String uri, String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertElementWithText( localName, uri, text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertElementWithText( localName, uri, text ); } finally { _locale.exit(); } } }
    public void insertAttribute ( String localName ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttribute( localName ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttribute( localName ); } finally { _locale.exit(); } } }
    public void insertAttribute ( String localName, String uri ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttribute( localName, uri ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttribute( localName, uri ); } finally { _locale.exit(); } } }
    public void insertAttribute ( QName name ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttribute( name ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttribute( name ); } finally { _locale.exit(); } } }
    public void insertAttributeWithValue ( String Name, String value ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttributeWithValue( Name, value ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttributeWithValue( Name, value ); } finally { _locale.exit(); } } }
    public void insertAttributeWithValue ( String name, String uri, String value ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttributeWithValue( name, uri, value ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttributeWithValue( name, uri, value ); } finally { _locale.exit(); } } }
    public void insertAttributeWithValue ( QName name, String value ) { if (_locale.noSync()) { _locale.enter(); try { _insertAttributeWithValue( name, value ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertAttributeWithValue( name, value ); } finally { _locale.exit(); } } }
    public void insertNamespace ( String prefix, String namespace ) { if (_locale.noSync()) { _locale.enter(); try { _insertNamespace( prefix, namespace ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertNamespace( prefix, namespace ); } finally { _locale.exit(); } } }
    public void insertComment ( String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertComment( text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertComment( text ); } finally { _locale.exit(); } } }
    public void insertProcInst ( String target, String text ) { if (_locale.noSync()) { _locale.enter(); try { _insertProcInst( target, text ); } finally { _locale.exit(); } } else synchronized ( _locale ) { _locale.enter(); try { _insertProcInst( target, text ); } finally { _locale.exit(); } } }
    
    //
    //
    //

    private Locale _locale;
    private Cur    _cur;
}