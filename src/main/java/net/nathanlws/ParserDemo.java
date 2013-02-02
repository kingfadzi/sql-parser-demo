/**
 * Copyright (c) 2013 Nathan Williams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package net.nathanlws;

import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.StatementNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ParserDemo
{
    private static final String DEMO_STATEMENTS =
            "SELECT * FROM t;" +
            "SELECT x, y, z FROM t;" +
            "SELECT t1.g, t2.h FROM t1,t2;" +
            "INSERT INTO t(a,s,d,f) VALUES (1,2,3,4);" +
            "UPDATE t SET x=1, y=2 WHERE z>100;" +
            "DELETE FROM t WHERE n=5 AND m=50;";


    public static void main(String[] args) throws Exception {
        String sql = null;
        boolean echoStatement = false;

        for(int i = 0; i < args.length; ++i) {
            String k = args[i];
            if("--help".equals(k)) {
                usage(false);
            } else if("--demo".equals(k)) {
                sql = DEMO_STATEMENTS;
            } else if("--echo".equals(k)) {
                echoStatement = true;
            } else if("--file".equals(k)) {
                if((i+1) == args.length) {
                    usage(true);
                }
                String name = args[++i];
                InputStream is = new FileInputStream(new File(name));
                sql = readStream(is);
                is.close();
            }
        }

        if(sql == null) {
            sql = readStream(System.in);
        }

        SQLParser parser = new SQLParser();
        List<StatementNode> nodes =  parser.parseStatements(sql);
        for(StatementNode node : nodes) {
            if(echoStatement) {
                System.out.println(sql.substring(node.getBeginOffset(), node.getEndOffset() + 1));
            }
            node.accept(new QueryTreeVisitor());
            System.out.println();
        }
    }

    public static void usage(boolean isError) {
        System.out.println("ParserDemo [--help] [--demo] [--echo] [--file <filename>]");
        System.exit(isError ? 1 : 0);
    }

    private static String readStream(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        StringBuilder builder = new StringBuilder();
        char buffer[] = new char[1024];
        // Wait for at least 1 byte (e.g. stdin)
        int n = reader.read(buffer);
        builder.append(buffer, 0, n);
        while(reader.ready()) {
            n = reader.read(buffer);
            builder.append(buffer, 0, n);
        }
        return builder.toString();
    }
}
