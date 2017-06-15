package com.jvm_bloggers.frontend.public_area.common_layout;

import com.jvm_bloggers.domain.query.newsletter_issue_for_listing.NewsletterIssueForListing;
import com.jvm_bloggers.domain.query.newsletter_issue_for_listing.NewsletterIssueForListingQuery;

import javaslang.collection.Seq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RightFrontendSidebarBackingBean {

    private final NewsletterIssueForListingQuery query;

    @Autowired
    public RightFrontendSidebarBackingBean(NewsletterIssueForListingQuery query) {
        this.query = query;
    }

    public Seq<NewsletterIssueForListing> getLatestIssues(int numberOfListedIssues) {
        return query.findLatestIssues(numberOfListedIssues);
    }
}
