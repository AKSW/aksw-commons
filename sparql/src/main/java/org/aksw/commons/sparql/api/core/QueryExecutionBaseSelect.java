package org.aksw.commons.sparql.api.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.engine.QueryExecutionBase;
import org.aksw.commons.jena.util.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * A Sparqler-class that implements ask, describe, and construct
 * based on the executeCoreSelect(Query) method.
 * 
 * Also, works on String and Query level.
 * 
 * Some of the code has been taken from 
 * com.hp.hpl.jena.sparql.engine.QueryExecutionBase, which is a
 * class with a similar purpose but not as reusable as this one
 * (This class reduces all operations to a single executeCoreSelect call)
 * 
 *
 * @author raven
 *
 */
public abstract class QueryExecutionBaseSelect
	extends QueryExecutionDecorator
{
	private static final Logger logger = LoggerFactory
			.getLogger(QueryExecutionBaseSelect.class);

    private Query query;

    public QueryExecutionBaseSelect(Query query) {
        super(null);
        this.query = query;
    }

    //private QueryExecution running = null;

	abstract protected QueryExecution executeCoreSelectX(Query query);
	
    protected ResultSet executeCoreSelect(Query query) {
        if(this.decoratee != null) {
            throw new RuntimeException("A query is already running");
        }

        this.decoratee = executeCoreSelectX(query);
        return decoratee.execSelect();
    }

	@Override
	public boolean execAsk() {
		if (!query.isAskType()) {
			throw new RuntimeException("ASK query expected. Got: ["
					+ query.toString() + "]");
		}

		Query selectQuery = QueryUtils.elementToQuery(query.getQueryPattern());
		selectQuery.setLimit(1);

		ResultSet rs = executeCoreSelect(selectQuery);

		long rowCount = 0;
		while(rs.hasNext()) {
			++rowCount;
		}

		if (rowCount > 1) {
			logger.warn("Received " + rowCount + " rows for the query ["
					+ query.toString() + "]");
		}

		return rowCount > 0;
	}

	@Override
	public Model execDescribe(Model result) {
		throw new RuntimeException("Sorry, DESCRIBE is not implemted yet.");
	}

	@Override
	public Model execConstruct(Model result) {
		if (!query.isConstructType()) {
			throw new RuntimeException("CONSTRUCT query expected. Got: ["
					+ query.toString() + "]");
		}

		
		//Query selectQuery = QueryUtils.elementToQuery(query.getQueryPattern());
		query.setQueryResultStar(true);
		ResultSet rs = executeCoreSelect(query);

		// insertPrefixesInto(result) ;
		Template template = query.getConstructTemplate();

		// Build each template substitution as triples.
		while(rs.hasNext()) {
			Set<Triple> set = new HashSet<Triple>();
			Map<Node, Node> bNodeMap = new HashMap<Node, Node>();
			Binding binding = rs.nextBinding();
			template.subst(set, bNodeMap, binding);

			// Convert and merge into Model.
			for (Iterator<Triple> iter = set.iterator(); iter.hasNext();) {
				Triple t = iter.next();
				Statement stmt = ModelUtils.tripleToStatement(result, t);
				if (stmt != null) {
					result.add(stmt);
				}
			}
		}
		
		return result;
	}

	@Override
	public ResultSet execSelect() {
		if (!query.isSelectType()) {
			throw new RuntimeException("SELECT query expected. Got: ["
					+ query.toString() + "]");
		}
		
		return executeCoreSelect(query);
	}
	
	//@Override
	public void executeUpdate(UpdateRequest updateRequest)
	{
		throw new RuntimeException("Not implemented");
	}
}