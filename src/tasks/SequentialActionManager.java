package tasks;

import java.util.ArrayList;
import java.util.List;

import datastructures.ListWrapper;

public class SequentialActionManager extends Action {
	List<Action> list = new ArrayList();

	public boolean add(Action a) {
		Action last = null;
		if (list.size() > 0)
			last = list.get(list.size()-1);
		boolean ret = list.add( a );
		if (last != null) {
			last.addNext( a );
			a.addPrevious( last );
		}

		return ret;
	}

	public void add(int index, Action a) {
		Action last = null;
		if (list.size() > 0)
			last = list.get(index);
		list.add( index, a );
		if (last != null) {
			last.addNext( a );
			a.addPrevious( last );
		}
	}

	public Action get(int n) {
		return list.get( n );
	}

	public int size() {
		return list.size();
	}

	public void innerRun() {
		if (size() == 0)
			return;
		addNext( get(0) );
		get(0).run();
	}
}
