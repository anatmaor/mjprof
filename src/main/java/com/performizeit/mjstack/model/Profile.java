package com.performizeit.mjstack.model;

import java.util.HashMap;

/**
 * Created by life on 5/5/14.
 */
public class Profile {
    public boolean color = false;
    SFNode root =     new SFNode();

    public Profile() {
        root.setColor(color);
        root.sf = null;
    }
    public  Profile(String parseString) {
        this();
        if (parseString.contains("]\\ ")) {
            parseMulti(parseString);
        }   else {
            parseSingle(parseString);

        }

    }
    public  void parseSingle(String stackTrace) {
        addSingle(stackTrace);

    }


    public void addSingle(String stackTrace) {

        String[] sf = stackTrace.split("\n");
        HashMap<String,SFNode> c = root.children;
        root.count ++;
        for (int i=sf.length-1;i>=0;i--) {

            String sfi = sf[i].trim();
            if (sfi.isEmpty()) continue;
            SFNode node = c.get(sfi);
            if (node == null) {
                node = new SFNode();

                node.sf = sfi;
                c.put(sfi, node);
            }
            node.count++;
            c = node.children;
        }
    }

    public  void parseMulti(String treeString) {
        String[] lines = treeString.split( "\n");
        nextFrame(-1,root, lines, 0);

    }



    private int nextFrame(int parentPehIndent,SFNode parent,  String[] lines, int curLine) {
        while (curLine < lines.length) {
//            System.out.println("curLine="+curLine + lines[curLine]);
            String line = lines[curLine];

            ProfileEntryHelper peh = new ProfileEntryHelper(line);

//            System.out.println("ind="+peh.indentation);
            if (peh.indentation <= parentPehIndent) {
                return curLine;
            } else if (peh.indentation > parentPehIndent) {
                SFNode node;
                node = parent.children.get(peh.description);
                if (node == null) {
                    node = new SFNode();
                    node.sf = peh.description;
                    parent.children.put(peh.description, node);
                }
                node.count += peh.count;
                if (peh.indentation ==0) parent.count += peh.count;
                curLine = nextFrame(peh.indentation, node, lines, curLine + 1);
            }
        }
        return curLine;

    }



    public void addMulti(Profile p) {
        root.mergeToNode(p.root);
    }
    @Override
    public String toString() {
        return root.toString();

    }




    public void visit(ProfileVisitor pv) {
         root.visitChildren(pv,0);
    }

    public void filter(ProfileNodeFilter pnf,Object context) {
        root.filterChildren(pnf,0,context);
    }

}