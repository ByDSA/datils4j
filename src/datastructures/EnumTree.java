package datastructures;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class EnumTree<E extends Enum<E>> extends EnumMap<E, EnumTree<E>> {
	protected EnumTree<E> parent = null;
	
	public EnumTree(Map<E, ? extends EnumTree<E>> m) {
		super( m );
	}
	
	public Class<E> type() {
		  try {
		    Field keyType = EnumMap.class.getDeclaredField("keyType");
		    keyType.setAccessible(true);
		    return (Class<E>) keyType.get(this);
		  } catch (IllegalAccessException | NoSuchFieldException e) {
		    throw new AssertionError("Could not find EnumMap type", e);
		  }
		}

	public EnumTree(Class<E> keyType) {
		super( keyType );
	}
	
	public EnumTree(EnumMap<E, ? extends EnumTree<E>> m) {
		super( m );
	}

	public EnumTree<E> addChildren(E value) {
		EnumTree<E> child = get( value );

		if (child == null) {			
			child = newEmptyNode();
			child.parent = this;
			put( value, child );
		}

		return child;
	}

	protected EnumTree<E> newEmptyNode() {
		return new EnumTree(getClass());
	}

	public EnumTree<E> addBranch(Iterable<E> values) {
		EnumTree<E> node = this;
		for (E v : values) {
			node = node.addChildren( v );
		}
		
		return node;
	}
	
	public EnumTree<E> getNode(Iterable<E> values) {
		EnumTree<E> node = this;
		for (E v : values) {
			if (node == null)
				return null;
			node = node.get( v );
		}
		
		return node;
	}

	public void forEachParent(Function<EnumTree<E>, Boolean> f) {
		if (parent != null)
			if ( f.apply( parent ) )
				parent.forEachParent( f );
	}
}
