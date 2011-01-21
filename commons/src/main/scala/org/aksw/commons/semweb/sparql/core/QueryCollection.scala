package org.aksw.commons.semweb.sparql.core

import com.hp.hpl.jena.query.QuerySolution
import collection.JavaConversions.JIterableWrapper


/**
 * A collection view on a result set for a Sparql-Query.
 * This collection uses limit and offset to retrieve the Sparql result
 * chunk-wise
 *
 * 
 * @author Claus Stadler
 *
 */
class QueryCollection(val sparqlEndpoint : SparqlEndpoint, val query : String, val partitionSize : Int)
	extends Iterable[QuerySolution]
{
  def this(sparqlEndpoint : SparqlEndpoint, query : String) = this(sparqlEndpoint, query, 0)
  //def this(graphDao : IGraphDao, query : String, partitionSize : Int) = this(graphDao, query, Some(partitionSize))

  def javaIterator[T](it: Iterator[T]) = new java.util.Iterator[T] {
    def hasNext() = it.hasNext
    def next() = it.next
    def remove() = throw new UnsupportedOperationException()
  }

  def javaIterable[T](iterable: Iterable[T]) = new java.lang.Iterable[T] {
    def iterator() = javaIterator(iterable.iterator)
  }


  def asJava() : java.lang.Iterable[QuerySolution] = javaIterable(this)

	override def iterator() : Iterator[QuerySolution] = {
		return new QueryResultIterator(sparqlEndpoint, query, partitionSize, 0);
	}
}

