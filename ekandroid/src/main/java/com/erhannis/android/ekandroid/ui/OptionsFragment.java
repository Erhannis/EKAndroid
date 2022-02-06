package com.erhannis.android.ekandroid.ui;

import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.Stringable;
import com.erhannis.mathnstuff.utils.Factory;
import com.erhannis.mathnstuff.utils.Options;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class OptionsFragment extends ListFragment<Stringable<Map.Entry<String, Object>>> {
    public static final String DEFAULT_OPTIONS_FILENAME = "options.dat";
    public final String optionsFilename;
    private final Options options;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OptionsFragment() {
        optionsFilename = null;
        options = null;
        //TODO Throw exception?
    }

    /*
    public OptionsFragment(Options options) {
        this(options, DEFAULT_OPTIONS_FILENAME);
    }

    public OptionsFragment(String message, Options options, String optionsFilename) {
        this(options, optionsFilename);
        this.labelMessage.setText(message);
    }

    public OptionsFragment(Options options, String optionsFilename) {
        this.optionsFilename = optionsFilename;
        this.options = options;
        this.listModel = new DefaultListModel<>();
        options.getOrDefault("OptionsFrame.AUTOSAVE_OPTIONS", true); // Preloading default
        reload();
        initComponents();
    }

    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();
        fragment.mRows = rows;
        fragment.onClickHandler = onClickHandler;
        fragment.onLongClickHandler = onLongClickHandler;
        fragment.setRetainInstance(true);
    }

    private void reload() {
        listModel.clear();
        FactoryHashMap<Object, HashSet<String>> reverse = new FactoryHashMap<>(new Factory<Object, HashSet<String>>() {
            @Override
            public HashSet<String> construct(Object input) {
                return new HashSet<String>();
            }
        });
        //Collections.sort
        for (Entry<String, Object> e : options.getRecentEntries()) {
            Stringable<Entry<String, Object>> s = new Stringable<Entry<String, Object>>(e, e.getKey() + " : " + e.getValue());
            listModel.addElement(s);
            reverse.get(s.val.getValue()).add(s.val.getKey());
        }
        for (Entry<Object, HashSet<String>> e : reverse.entrySet()) {
            if (e.getValue().size() > 1) {
                System.err.println("Conflict on " + e.getKey() + " with " + String.join(", ", e.getValue()));
            }
        }
    }

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        reload();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void listOptionsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listOptionsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            select();
        } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }//GEN-LAST:event_listOptionsKeyPressed
     */
}
