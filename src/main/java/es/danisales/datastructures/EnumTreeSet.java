package es.danisales.datastructures;

import java.util.EnumSet;
import java.util.function.Function;

public class EnumTreeSet<TreeNode extends Enum<TreeNode>, Content extends Enum<Content>> extends EnumTree<TreeNode> {
	EnumSet<Content> nodeContent;
	EnumSet<Content> childrenContent;
	Class<Content> contentType;

	public EnumTreeSet(Class<TreeNode> type, Class<Content> cType) {
		super( type );
		contentType = cType;
		nodeContent = EnumSet.noneOf( contentType );
		childrenContent = EnumSet.noneOf( contentType );
	}

	public void addContent(final Content c) {
		nodeContent.add( c );

		final EnumTreeSet<TreeNode, Content> castedThis = (EnumTreeSet<TreeNode, Content>) this;
		forEachParent(new Function<EnumTree<TreeNode>, Boolean>() {
			@Override
			public Boolean apply(EnumTree<TreeNode> p) {
				castedThis.childrenContent.add( c );

				return true;
			}
		});
	}

	@Override
	public EnumTreeSet<TreeNode, Content> getNode(Iterable<TreeNode> values) {
		return (EnumTreeSet<TreeNode, Content>) super.getNode( values );
	}

	public EnumSet<Content> getContent() {
		return nodeContent;
	}

	public EnumSet<Content> getChildrenContent() {
		return childrenContent;
	}

	@Override
	protected EnumTreeSet<TreeNode, Content> newEmptyNode() {
		return new EnumTreeSet(type(), contentType);
	}

	public void removeContent(final Content c) {
		nodeContent.remove( c );
		final EnumTreeSet<TreeNode, Content> castedThis = (EnumTreeSet<TreeNode, Content>) this;
		forEachParent(new Function<EnumTree<TreeNode>, Boolean>() {
			@Override
			public Boolean apply(EnumTree<TreeNode> p) {
				castedThis.childrenContent.remove( c );

				return true;
			}
		});
	}

	public void addContent(Content c, Iterable<TreeNode> nodes) {
		EnumTreeSet<TreeNode, Content> leaf = (EnumTreeSet<TreeNode, Content>) addBranch(nodes);
		leaf.addContent( c );
	}

	public void removeContent(Content c, Iterable<TreeNode> nodes) {
		EnumTreeSet<TreeNode, Content> leaf = (EnumTreeSet<TreeNode, Content>) getNode(nodes);
		leaf.removeContent( c );
	}
}
