%
%
%
function plot_output2( name, legend_title, y_title, data, x_col, y_col, chart_col, lval )

    colors = ['r' 'g' 'b' 'c' 'm' 'y' 'k' ]
    plts = unique(data(:, chart_col));

    fig = figure(1)
        
%     hTitle  = title ('My Publication-Quality Graphics');
% hXLabel = xlabel('Length (m)'                     );
% hYLabel = ylabel('Mass (kg)'                      );
% 
% //hText   = text(10, 800, ...
%   //sprintf('\\it{C = %0.1g \\pm %0.1g (CI)}', ...
%   //c, cint(2)-c));
% 
% hLegend = legend( ...
%   [hE, hFit, hData, hModel, hCI(1)], ...
%   'Data (\mu \pm \sigma)' , ...
%   'Fit (\it{C x^3})'      , ...
%   'Validation Data'       , ...
%   'Model (\it{C x^3})'    , ...
%   '95% CI'                , ...
%   'location', 'NorthWest' );
% 
%     set( gca                       , ...
%     'FontName'   , 'Helvetica' );
%  set([hTitle, hXLabel, hYLabel, hText], ...
%      'FontName'   , 'AvantGarde');
% set([hLegend, gca]             , ...
%     'FontSize'   , 8           );
% set([hXLabel, hYLabel, hText]  , ...
%     'FontSize'   , 10          );
% set( hTitle                    , ...
%     'FontSize'   , 12          , ...
%     'FontWeight' , 'bold'      );
% 
%     set(gca, ...
%   'Box'         , 'off'     , ...
%   'TickDir'     , 'out'     , ...
%   'TickLength'  , [.02 .02] , ...
%   'XMinorTick'  , 'on'      , ...
%   'YMinorTick'  , 'on'      , ...
%   'YGrid'       , 'on'      , ...
%   'XColor'      , [.3 .3 .3], ...
%   'YColor'      , [.3 .3 .3], ...
%   'YTick'       , 0:500:2500, ...
%   'LineWidth'   , 1         );


    numPlts = size(plts, 1);
    h = zeros(numPlts, numPlts);
    l = zeros(numPlts, 1);
    for i = 1:numPlts
        split = plts(i);
        
        rows = find(data(:, chart_col) == plts(i));

        colorInx = i % size(colors, 2); 
        lineSpec = sprintf('x-%s', colors(colorInx));
        h(:, i) = plot(data(rows, x_col), data(rows, y_col), lineSpec);
        max_x = max(data(:, x_col));
        axis([0 max_x 0 max([1; data(:, y_col)])]);
        if max_x > 10000
            set(gca, 'XTickLabel', num2str(get(gca,'XTick')','%d'))  %#'
        end
        %xticklabel_rotate([],45)                  %# rotate the xtick-labels 45 degrees

        l(i) = split;
        
        hold on;

    end
    hold off;
    xlabel(name);
    ylabel(y_title);
    if isempty(lval)
        lval = strcat(int2str(l), '%');
    end
    lh = legend(h(1, :), lval);
%     set(lh,'position',get(lh,'position').*[1 0.90 1 1]);
    set(lh,'location','eastoutside');
    v = get(lh,'title');
    set(v,'string', legend_title);
    fileprefix = strrep(lower(name), ' ', '_');
    filename   = sprintf('img/%s.jpg', fileprefix);
    print(fig, '-djpeg', filename);
end

