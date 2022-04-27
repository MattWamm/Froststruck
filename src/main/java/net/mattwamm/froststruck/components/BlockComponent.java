package net.mattwamm.froststruck.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.mattwamm.froststruck.util.IsolatedRoom;

public interface BlockComponent extends Component {
    int getValue();
    void setValue(IsolatedRoom room);
}
