package org.csstudio.yamcs.ycl.dsl.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalYCLLexer extends Lexer {
    public static final int RULE_HEX=8;
    public static final int RULE_ID=6;
    public static final int RULE_WS=10;
    public static final int RULE_EXT_INT=5;
    public static final int RULE_STRING=7;
    public static final int RULE_SL_COMMENT=9;
    public static final int RULE_INT=4;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int EOF=-1;

    // delegates
    // delegators

    public InternalYCLLexer() {;} 
    public InternalYCLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalYCLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g"; }

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:11:7: ( '.' )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:11:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:12:7: ( '(' )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:12:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:13:7: ( ')' )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:13:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:14:7: ( '=' )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:14:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "RULE_EXT_INT"
    public final void mRULE_EXT_INT() throws RecognitionException {
        try {
            int _type = RULE_EXT_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:385:14: ( RULE_INT ( 'e' | 'E' ) ( '-' | '+' ) RULE_INT )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:385:16: RULE_INT ( 'e' | 'E' ) ( '-' | '+' ) RULE_INT
            {
            mRULE_INT(); 
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            mRULE_INT(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EXT_INT"

    // $ANTLR start "RULE_ID"
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:387:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:387:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:387:11: ( '^' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='^') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:387:11: '^'
                    {
                    match('^'); 

                    }
                    break;

            }

            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:387:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ID"

    // $ANTLR start "RULE_INT"
    public final void mRULE_INT() throws RecognitionException {
        try {
            int _type = RULE_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:389:10: ( ( '0' .. '9' )+ )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:389:12: ( '0' .. '9' )+
            {
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:389:12: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:389:13: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_INT"

    // $ANTLR start "RULE_HEX"
    public final void mRULE_HEX() throws RecognitionException {
        try {
            int _type = RULE_HEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:391:10: ( '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:391:12: '0' ( 'x' | 'X' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:391:26: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')||(LA4_0>='A' && LA4_0<='F')||(LA4_0>='a' && LA4_0<='f')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_HEX"

    // $ANTLR start "RULE_STRING"
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:13: ( ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:15: ( '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='\"') ) {
                alt7=1;
            }
            else if ( (LA7_0=='\'') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:16: '\"' ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:20: ( '\\\\' . | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop5:
                    do {
                        int alt5=3;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='\\') ) {
                            alt5=1;
                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFF')) ) {
                            alt5=2;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:21: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:28: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:48: '\\'' ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:53: ( '\\\\' . | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop6:
                    do {
                        int alt6=3;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='\\') ) {
                            alt6=1;
                        }
                        else if ( ((LA6_0>='\u0000' && LA6_0<='&')||(LA6_0>='(' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='\uFFFF')) ) {
                            alt6=2;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:54: '\\\\' .
                    	    {
                    	    match('\\'); 
                    	    matchAny(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:393:61: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_STRING"

    // $ANTLR start "RULE_SL_COMMENT"
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:17: ( '#' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:19: '#' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match('#'); 
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:23: (~ ( ( '\\n' | '\\r' ) ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\u0000' && LA8_0<='\t')||(LA8_0>='\u000B' && LA8_0<='\f')||(LA8_0>='\u000E' && LA8_0<='\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:23: ~ ( ( '\\n' | '\\r' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:39: ( ( '\\r' )? '\\n' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\n'||LA10_0=='\r') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:40: ( '\\r' )? '\\n'
                    {
                    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:40: ( '\\r' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='\r') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:395:40: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SL_COMMENT"

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:397:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:397:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:397:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WS"

    public void mTokens() throws RecognitionException {
        // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:8: ( T__11 | T__12 | T__13 | T__14 | RULE_EXT_INT | RULE_ID | RULE_INT | RULE_HEX | RULE_STRING | RULE_SL_COMMENT | RULE_WS )
        int alt12=11;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:10: T__11
                {
                mT__11(); 

                }
                break;
            case 2 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:16: T__12
                {
                mT__12(); 

                }
                break;
            case 3 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:22: T__13
                {
                mT__13(); 

                }
                break;
            case 4 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:28: T__14
                {
                mT__14(); 

                }
                break;
            case 5 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:34: RULE_EXT_INT
                {
                mRULE_EXT_INT(); 

                }
                break;
            case 6 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:47: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 7 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:55: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 8 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:64: RULE_HEX
                {
                mRULE_HEX(); 

                }
                break;
            case 9 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:73: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 10 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:85: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 11 :
                // ../org.csstudio.yamcs.ycl.dsl/src-gen/org/csstudio/yamcs/ycl/dsl/parser/antlr/internal/InternalYCL.g:1:101: RULE_WS
                {
                mRULE_WS(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\5\uffff\1\14\1\uffff\1\14\6\uffff";
    static final String DFA12_eofS =
        "\16\uffff";
    static final String DFA12_minS =
        "\1\11\4\uffff\1\60\1\uffff\1\60\6\uffff";
    static final String DFA12_maxS =
        "\1\172\4\uffff\1\170\1\uffff\1\145\6\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\6\1\uffff\1\11\1\12\1\13\1\10\1\7\1\5";
    static final String DFA12_specialS =
        "\16\uffff}>";
    static final String[] DFA12_transitionS = {
            "\2\12\2\uffff\1\12\22\uffff\1\12\1\uffff\1\10\1\11\3\uffff\1\10\1\2\1\3\4\uffff\1\1\1\uffff\1\5\11\7\3\uffff\1\4\3\uffff\32\6\3\uffff\2\6\1\uffff\32\6",
            "",
            "",
            "",
            "",
            "\12\7\13\uffff\1\15\22\uffff\1\13\14\uffff\1\15\22\uffff\1\13",
            "",
            "\12\7\13\uffff\1\15\37\uffff\1\15",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__11 | T__12 | T__13 | T__14 | RULE_EXT_INT | RULE_ID | RULE_INT | RULE_HEX | RULE_STRING | RULE_SL_COMMENT | RULE_WS );";
        }
    }
 

}