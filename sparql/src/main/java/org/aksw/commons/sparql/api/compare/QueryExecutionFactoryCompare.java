package org.aksw.commons.sparql.api.compare;


import org.aksw.commons.sparql.api.core.QueryExecutionFactory;
import org.aksw.commons.sparql.api.core.QueryExecutionFactoryBackQuery;
import org.aksw.commons.sparql.api.core.QueryExecutionStreaming;
import org.aksw.commons.sparql.api.core.QueryExecutionStreamingWrapper;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

/**
 * A query execution factory, which generates query executions
 * that delay execution
 *
 *
 * @author Claus Stadler
 *
 *
 *         Date: 7/26/11
 *         Time: 10:27 AM
 */
public class QueryExecutionFactoryCompare
        extends QueryExecutionFactoryBackQuery { // We need to be able to parse queries in order to get the ordering
    private QueryExecutionFactory a;
    private QueryExecutionFactory b;

    // Whether to remove limit and offset from queries
    private boolean removeSlices = false;

    public QueryExecutionFactoryCompare(QueryExecutionFactory a, QueryExecutionFactory b) {
        this(a, b, false);
    }

    public QueryExecutionFactoryCompare(QueryExecutionFactory a, QueryExecutionFactory b, boolean removesSlices) {
        this.a = a;
        this.b = b;
        this.removeSlices = removesSlices;
    }

    @Override
    public QueryExecutionStreaming createQueryExecution(Query query) {

        if(removeSlices) {
            query = (Query)query.clone();


            query.setLimit(Query.NOLIMIT);
            query.setOffset(0);
        }

        //boolean isOrdered = !query.getOrderBy().isEmpty();
        QueryExecution tmp = new QueryExecutionCompare(query, a.createQueryExecution(query), b.createQueryExecution(query), false);
        QueryExecutionStreaming result = QueryExecutionStreamingWrapper.wrap(tmp);
        return result;
    }

    /*
    @Override
    public QueryExecution createQueryExecution(String queryString) {
        return new QueryExecutionCompare(a.createQueryExecution(queryString), b.createQueryExecution(queryString), false);
    }*/

    @Override
    public String getId() {
        return "compare(" + a.getId() + ", " + b.getId() + ")";
    }

    @Override
    public String getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
