package it.wintry.customblocks.objects;

import lombok.Data;
import org.bukkit.Instrument;
import org.bukkit.Note;

@Data
public class CustomBlock {

    private final String name;
    private final Instrument instrument;
    private final Note note;
    private final int modelData;

}
