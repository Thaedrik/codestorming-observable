/*
 * Copyright (c) 2012-2017 Codestorming.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Codestorming - initial API and implementation
 */
package org.codestorming.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link ObservableList} implementation by wrapping an existing {@link List}.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class ObservableListWrapper<E> extends AbstractObservableCollectionWrapper<E> implements ObservableList<E> {

	private final List<E> wrappedList;

	public ObservableListWrapper(List<E> wrapped) {
		super(wrapped);
		wrappedList = wrapped;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean added = wrappedList.addAll(index, c);
		if (added) {
			fireChange(create(CollectionChange.ChangeType.ADD, c));
		}
		return added;
	}

	@Override
	public E get(int index) {
		return wrappedList.get(index);
	}

	@Override
	public E set(int index, E element) {
		E removed = wrappedList.set(index, element);
		List<CollectionChange<E>> changes = new ArrayList<>(2);
		if (removed != null) {
			changes.add(new CollectionChange<>(CollectionChange.ChangeType.REMOVE, removed));
		}
		changes.add(new CollectionChange<>(CollectionChange.ChangeType.ADD, element));
		fireChange(changes);
		return removed;
	}

	@Override
	public void add(int index, E element) {
		wrappedList.add(index, element);
		fireChange(Collections.singletonList(new CollectionChange<>(CollectionChange.ChangeType.ADD, element)));
	}

	@Override
	public E remove(int index) {
		E removed = wrappedList.remove(index);
		if (removed != null) {
			fireChange(Collections.singletonList(new CollectionChange<>(CollectionChange.ChangeType.REMOVE, removed)));
		}
		return removed;
	}

	@Override
	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new AbstractListIteratorWrapper<E>(wrappedList.listIterator(index)) {
			@Override
			public void remove() {
				iter.remove();
				fireChange(Collections.singletonList(new CollectionChange<>(CollectionChange.ChangeType.REMOVE, last)));
			}

			@Override
			public void set(E e) {
				iter.set(e);
				List<CollectionChange<E>> changes = new ArrayList<>(2);
				if (last != null) {
					changes.add(new CollectionChange<>(CollectionChange.ChangeType.REMOVE, last));
				}
				changes.add(new CollectionChange<>(CollectionChange.ChangeType.ADD, e));
				fireChange(changes);
			}

			@Override
			public void add(E e) {
				iter.add(e);
				fireChange(Collections.singletonList(new CollectionChange<>(CollectionChange.ChangeType.ADD, e)));
			}
		};
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return wrappedList.subList(fromIndex, toIndex);
	}
}
